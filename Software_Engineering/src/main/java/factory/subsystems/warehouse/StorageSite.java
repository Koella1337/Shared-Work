package factory.subsystems.warehouse;

import java.awt.Graphics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.w3c.dom.Element;

import database.Database;
import database.StorageSiteTable;
import factory.shared.Constants;
import factory.shared.Constants.PlaceableSize;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.Task;
import factory.shared.Utils;
import factory.shared.enums.Material;
import factory.shared.interfaces.Placeable;

public class StorageSite implements Placeable {
	
	private final WarehouseSystem warehouseSystem;
	private final StorageSiteTable dbTable;
	private final WarehouseForklift forklift;
	
	private final int id;
	private final Position pos;
	
	private final List<Placeable> placeables;
	private final List<Shelf> shelves;
	private final List<Task> tasks;		//TODO: PriorityQueue ?
	
	private final ResourceBox inputbox;
	private final ResourceBox outputbox;
	
	public StorageSite(WarehouseSystem warehouseSystem, int id, Element xmlStorageSiteElem) {
		Objects.requireNonNull(xmlStorageSiteElem);
		
		//xml init
		final PlaceableSize boxSize = PlaceableSize.RESOURCE_BOX;
		this.pos = Utils.xmlGetPositionFromElement(xmlStorageSiteElem);
		this.inputbox = new ResourceBox(Utils.assignSize(Utils.xmlGetPositionFromFirstChild(xmlStorageSiteElem, "inputbox"), boxSize));
		this.outputbox = new ResourceBox(Utils.assignSize(Utils.xmlGetPositionFromFirstChild(xmlStorageSiteElem, "outputbox"), boxSize));
		
		System.out.printf("%d-inbox : %s%n", id, inputbox.getPosition().toString());	//TODO: remove
		System.out.printf("%d-outbox: %s%n", id, outputbox.getPosition().toString());
		
		//general init
		this.warehouseSystem = Objects.requireNonNull(warehouseSystem);
		this.forklift = new WarehouseForklift(new Position(pos.xPos, pos.yPos));
		this.id = id;
		this.tasks = new ArrayList<>();
		
		//interior init
		this.placeables = new ArrayList<>();
		placeables.add(inputbox);
		placeables.add(outputbox);
		this.shelves = buildShelves();
		placeables.addAll(shelves);
		
		//database init
		this.dbTable = new StorageSiteTable(id);
		Database.INSTANCE.addTable(dbTable);
	}
	
	/** Creates as many shelves as possible to fit into this StorageSite's interior. */
	private List<Shelf> buildShelves() {
		final List<Shelf> shelves = new ArrayList<>();
		final PlaceableSize shelfSize = PlaceableSize.SHELF;
		
		final int shelfWidth = shelfSize.x;
		final int shelfHeight = shelfSize.y;
		final int shelfOffset = Constants.SHELF_OFFSET;
		
		final Position ibox = inputbox.getPosition().clone();
		ibox.xPos -= shelfOffset/2; ibox.xSize += shelfOffset;
		ibox.yPos -= shelfOffset/2; ibox.ySize += shelfOffset;
		final Position obox = outputbox.getPosition().clone();
		obox.xPos -= shelfOffset/2; obox.xSize += shelfOffset;
		obox.yPos -= shelfOffset/2; obox.ySize += shelfOffset;
		
		Position nextShelfPos = Utils.assignSize(new Position(pos.xPos + shelfOffset, pos.yPos + shelfOffset), shelfSize);
		
		while ( (nextShelfPos.xPos + shelfWidth) < (pos.xPos + pos.xSize - shelfOffset) ) {
			//build shelf column
			while ( (nextShelfPos.yPos + shelfHeight) < (pos.yPos + pos.ySize - shelfOffset) ) {
				//make sure no shelf overlaps with in-/output box
				if ( !(Position.isOverlapping(nextShelfPos, ibox) || Position.isOverlapping(nextShelfPos, obox)) ) {
					shelves.add(new Shelf(Utils.assignSize(nextShelfPos.clone(), shelfSize)));
				}
				nextShelfPos.yPos += shelfHeight;
			}
			//prepare for building next column
			nextShelfPos.xPos += shelfWidth + shelfOffset;
			nextShelfPos.yPos = pos.yPos + shelfOffset;
		}
		
		return Collections.unmodifiableList(shelves);
	}

	public int getId() {
		return id;
	}
	
	public List<Placeable> getPlaceables() {
		return placeables;
	}
	
	protected int getContainerAmount(Material material) {
		try {
			return dbTable.getContainerAmount(material);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Accepts or rejects a task depending on whether the StorageSite is capable of<br>
	 * doing it within the deadline while also taking care of boxes and shelves.
	 * @return 
	 * 		"-1" ... required material not stored<br>
	 * 		" 0" ... task accepted <br>
	 * 		">0" ... rejected (overworked task count)
	 */
	protected int canAcceptTask(WarehouseTask task) {
		int containerAmount = getContainerAmount(task.material);
		if (containerAmount > 0)
			return tasks.size();
		else
			return -1;
	}
	
	/** Receive a Task from the WarehouseSystem. */
	protected void receiveTask(WarehouseTask task) {
		tasks.add(task);	//TODO
	}
	
	//TODO: testing method, remove later
	public void simulateTaskDone() {
		warehouseSystem.taskCompleted(this, new WarehouseTask(600000, Material.CAR_BODIES));
	}

	@Override //TODO
	public Position getPosition() {
		return this.pos;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Constants.UI_BORDER_COLOR);
		g.drawRect(0, 0, this.pos.xSize, this.pos.ySize);
		g.drawString("StorageSite "+id, 10, 10);			//TODO: remove?
	}

	protected ResourceBox getOutputbox() {
		return outputbox;
	}

	protected ResourceBox getInputbox() {
		return inputbox;
	}
	
}
