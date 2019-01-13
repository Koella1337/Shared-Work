package factory.subsystems.warehouse;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.w3c.dom.Element;

import database.Database;
import database.StorageSiteTable;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.Utils;
import factory.shared.interfaces.Placeable;

public class StorageSite implements Placeable {
	
	private final WarehouseSystem warehouseSystem;
	private final StorageSiteTable dbTable;
	
	private final int id;
	private Position pos;
	
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
		this.pos = Utils.xmlGetPositionFromElement(xmlStorageSiteElem);
		this.inputbox = new ResourceBox(Utils.xmlGetPositionFromFirstChild(xmlStorageSiteElem, "inputbox"));
		this.outputbox = new ResourceBox(Utils.xmlGetPositionFromFirstChild(xmlStorageSiteElem, "outputbox"));
		
		//interior init
		placeables = new ArrayList<>();
		placeables.add(inputbox);
		placeables.add(outputbox);
		placeables.addAll(buildShelves());
	}
	
	/** Creates as many shelves as possible to fit into this StorageSite's interior. */
	private List<Shelf> buildShelves() {
		
		
		return null;
	}

	public int getId() {
		return id;
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
		warehouseSystem.taskCompleted(this, new WarehouseTask());
	}

	@Override //TODO
	public Position getPosition() {
		return this.pos;
	}

	@Override  //TODO
	public void draw(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		g.drawRect(0, 0, this.pos.xSize,this.pos.ySize);
		g.drawString("StorageSite id:"+id, 20, 20);
		
		this.outputbox.draw(g);//TODO
		this.inputbox.draw(g);//TODO
		
	}

	protected ResourceBox getOutputbox() {
		return outputbox;
	}

	protected ResourceBox getInputbox() {
		return inputbox;
	}
	
}
