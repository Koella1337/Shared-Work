package factory.subsystems.warehouse;

import java.util.Objects;

import org.w3c.dom.Element;

import database.Database;
import database.StorageSiteTable;

public class StorageSite {
	
	private final WarehouseSystem warehouseSystem;
	private final StorageSiteTable dbTable;
	private final int id;

	public StorageSite(WarehouseSystem warehouseSystem, int id, Element xmlWarehouseElem) {
		Objects.requireNonNull(warehouseSystem);
		Objects.requireNonNull(xmlWarehouseElem);
		
		this.warehouseSystem = warehouseSystem;
		this.id = id;
		
		this.dbTable = new StorageSiteTable(id);
		Database.INSTANCE.addTable(dbTable);
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
	public int canAcceptTask(WarehouseTask task) {
		return 0;	//TODO
	}
	
	/** Receive a Task from the WarehouseSystem. */
	public void receiveTask(WarehouseTask task) {
		
	}
	
	//TODO: testing method, remove later
	public void simulateTaskDone() {
		warehouseSystem.taskCompleted(this, new WarehouseTask());
	}
	
}