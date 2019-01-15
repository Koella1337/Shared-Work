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
import factory.shared.TaskQueue;
import factory.shared.Utils;
import factory.shared.enums.Material;
import factory.shared.enums.MaterialStatus;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;

public class StorageSite implements Placeable, Stoppable {
	
	private final WarehouseSystem warehouseSystem;
	private final StorageSiteTable dbTable;
	private final WarehouseForklift forklift;
	
	private final int id;
	private final Position pos;
	
	private final List<Placeable> placeables;
	private final List<Shelf> shelves;
	private final TaskQueue<WarehouseTask> tasks;		//TODO: PriorityQueue ?
	
	private final ResourceBox inputbox;
	private final ResourceBox outputbox;
	
	public StorageSite(WarehouseSystem warehouseSystem, int id, Element xmlStorageSiteElem) {
		Objects.requireNonNull(xmlStorageSiteElem);
		this.warehouseSystem = Objects.requireNonNull(warehouseSystem);
		
		//xml init
		this.pos = Utils.xmlGetPositionFromElement(xmlStorageSiteElem);
		this.inputbox = new ResourceBox(warehouseSystem, Utils.xmlGetPositionFromFirstChild(xmlStorageSiteElem, "inputbox"));
		this.outputbox = new ResourceBox(warehouseSystem, Utils.xmlGetPositionFromFirstChild(xmlStorageSiteElem, "outputbox"));
		
		//general init
		this.id = id;
		this.tasks = new TaskQueue<>();
		this.forklift = new WarehouseForklift(this, new Position(pos.xPos, pos.yPos));
		
		//interior init
		this.placeables = new ArrayList<>();
		placeables.add(inputbox);
		placeables.add(outputbox);
		this.shelves = buildShelves();
		placeables.addAll(shelves);
		placeables.add(forklift);		//add forklift last so it is drawn over other Placeables
		
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
		
		return shelves;
	}

	public int getId() {
		return id;
	}
	
	public List<Placeable> getPlaceables() {
		return placeables;
	}
	
	/** Returns a shelf with at least one free ItemSlot. */
	protected Shelf getFreeShelf() {
		for (Shelf shelf : shelves) {
			if (shelf.hasFreeSlot())
				return shelf;
		}
		throw new IllegalArgumentException(this.toString() + " is completely full!");
	}
	
	/** Returns a shelf from which the specified material can be fetched. */
	protected Shelf getShelfForMaterial(Material mat) {
		for (Shelf shelf : shelves) {
			if (shelf.hasMaterial(mat))
				return shelf;
		}
		throw new IllegalArgumentException("Material \"" + mat + "\" not present in " + this.toString());
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
		int containerAmount = getContainerAmount(task.getMaterial());
		if (containerAmount > 0)
			return tasks.size();
		else
			return -1;
	}
	
	/** Receive a Task from the WarehouseSystem. */
	protected void receiveTask(WarehouseTask task) {
		tasks.offer(task);	
		//TODO remove material from dbTable
	}
	
	protected ResourceBox getOutputbox() {
		return outputbox;
	}

	protected ResourceBox getInputbox() {
		return inputbox;
	}
	
	//TODO: testing method, remove later
	public void simulateTaskDone() {
		warehouseSystem.taskCompleted(this, new WarehouseTask(600000, Material.CAR_BODIES));
	}

	@Override
	public Position getPosition() {
		return this.pos;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Constants.UI_BORDER_COLOR);
		g.drawRect(0, 0, pos.xSize, pos.ySize);
		g.drawString(this.toString(), 10, 10);
	}

	@Override
	public void start() {
		try {
			//continous loop as daemon thread --> only stops at program exit
			while (true) {
				if (warehouseSystem.getStatus() == SubsystemStatus.RUNNING) {
					WarehouseTask task = tasks.poll();
					if (task != null) {
						forklift.doTask(task);
					}
					else {
						//TODO inputbox management
						Collections.sort(shelves, (shelf1, shelf2) -> {
							return MaterialStatus.comparator().compare(shelf1.getShelfSortedness(), shelf2.getShelfSortedness());
						});
						
						Shelf shelfToSort = shelves.get(0);
						MaterialStatus shelfStatus = shelfToSort.getShelfSortedness();
						
						//TODO: decide between shelf-sorting and inputbox management
						
						if ( !(shelfStatus == MaterialStatus.PERFECT || shelfStatus == MaterialStatus.EMPTY) )
							forklift.sortShelf(shelfToSort);
					}
				}
				else {
					Thread.sleep(1000);	//wait before querying status again
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		
	}
	
	@Override
	public String toString() {
		return "StorageSite " + this.id;
	}
	
}
