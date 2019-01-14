package factory.subsystems.warehouse;

import java.awt.Color;
import java.awt.Graphics;
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
import factory.shared.Utils;
import factory.shared.enums.Material;
import factory.shared.interfaces.Placeable;

public class StorageSite implements Placeable {
	
	private final WarehouseSystem warehouseSystem;
	private final StorageSiteTable dbTable;
	
	private final int id;
	private final Position pos;
	
	private final List<Placeable> placeables;
	private final ResourceBox inputbox;
	private final ResourceBox outputbox;
	
	public StorageSite(WarehouseSystem warehouseSystem, int id, Element xmlStorageSiteElem) {
		Objects.requireNonNull(warehouseSystem);
		Objects.requireNonNull(xmlStorageSiteElem);
		
		//general init
		this.warehouseSystem = warehouseSystem;
		this.id = id;
		
		//database init
		this.dbTable = new StorageSiteTable(id);
		Database.INSTANCE.addTable(dbTable);
		
		//xml init
		final PlaceableSize boxSize = PlaceableSize.RESOURCE_BOX;
		this.pos = Utils.xmlGetPositionFromElement(xmlStorageSiteElem);
		this.inputbox = new ResourceBox(Utils.assignSize(Utils.xmlGetPositionFromFirstChild(xmlStorageSiteElem, "inputbox"), boxSize));
		this.outputbox = new ResourceBox(Utils.assignSize(Utils.xmlGetPositionFromFirstChild(xmlStorageSiteElem, "outputbox"), boxSize));
		
		System.out.printf("%d-inbox : %s%n", id, inputbox.getPosition().toString());	//TODO: remove
		System.out.printf("%d-outbox: %s%n", id, outputbox.getPosition().toString());
		
		//interior init
		placeables = new ArrayList<>();
		placeables.add(inputbox);
		placeables.add(outputbox);
		
		List<Shelf> x = buildShelves();
		System.out.println("x size: " + x.size());
		
		placeables.addAll(x);
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
	
	protected boolean hasMaterial(Material material) {
		//TODO
		return true;
	}
	
	/**
	 * Accepts or rejects a task depending on whether the StorageSite is capable of<br>
	 * doing it within the deadline while also taking care of boxes and shelves.
	 * @return 
	 * 		the amount of tasks this warehouse needs to complete before being<br>
	 * 		able to accept another task. (0 = task accepted, >0 = rejected)
	 */
	protected int canAcceptTask(WarehouseTask task) {
		return 0;	//TODO
	}
	
	/** Receive a Task from the WarehouseSystem. */
	protected void receiveTask(WarehouseTask task) {
		
	}
	
	//TODO: testing method, remove later
	public void simulateTaskDone() {
		warehouseSystem.taskCompleted(this, new WarehouseTask(600000, Material.CAR_BODIES));
	}

	@Override //TODO
	public Position getPosition() {
		return this.pos;
	}

	@Override  //TODO
	public void draw(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		g.drawRect(0, 0, this.pos.xSize, this.pos.ySize);
		g.drawString("StorageSite id:"+id, 10, 10);			//TODO: remove
	}

	protected ResourceBox getOutputbox() {
		return outputbox;
	}

	protected ResourceBox getInputbox() {
		return inputbox;
	}
	
}
