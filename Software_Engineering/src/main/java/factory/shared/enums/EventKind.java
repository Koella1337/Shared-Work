package factory.shared.enums;

import factory.shared.interfaces.ContainerSupplier;
import factory.shared.Task;
import factory.shared.ResourceBox;
import factory.subsystems.agv.AgvTask;
import factory.subsystems.agv.Forklift;
import factory.subsystems.assemblyline.Conveyor;
import factory.subsystems.assemblyline.Robot;
import factory.subsystems.warehouse.WarehouseTask;

/**
 * The kind of a FactoryEvent.<br>
 * EventKinds can portray simple Notifications (e.g. a Task being completed), but can also be errors.
 */
public enum EventKind {
	//----------------------------------- Generic Events -----------------------------------
	TASK_NOT_COMPLETED_BEFORE_DEADLINE (EventSeverity.INFO,		Task.class),		//TODO: fire this event
	
	RESOURCEBOX_FULL				(EventSeverity.ERROR, 		ResourceBox.class),
	RESOURCEBOX_ALMOST_FULL			(EventSeverity.IMPORTANT, 	ResourceBox.class),
	
	//------------------------------- RobotArms Notifications -------------------------------
	CAR_FINISHED 					(EventSeverity.NORMAL, 		Material.class, Robot.class),
	TASK_FINISHED 					(EventSeverity.NORMAL),
	ROBOTARMS_LACK_OF_MATERIAL 		(EventSeverity.NORMAL, 		Material.class, Robot.class),
	
	
	//---------------------------------- RobotArms Errors -----------------------------------
	ROBOTARMS_BROKEN				(EventSeverity.IMPORTANT, 	Robot.class),
	
	//------------------------------- Conveyors Notifications -------------------------------
	CONVEYORS_LACK_OF_OIL			(EventSeverity.IMPORTANT, 	Conveyor.class),
	CONVEYOR_PICK_UP_BOX			(EventSeverity.NORMAL, Material.class, Conveyor.class),
	
	//---------------------------------- Conveyors Errors -----------------------------------
	CONVEYORS_BROKEN				(EventSeverity.IMPORTANT, 	Conveyor.class),
	
	
	//------------------------------- Warehouse Notifications -------------------------------
	WAREHOUSE_TASK_COMPLETED		(EventSeverity.NORMAL,		WarehouseTask.class, ContainerSupplier.class),
	
	//---------------------------------- Warehouse Errors -----------------------------------
	
	
	
	//---------------------------------- AGV Notifications ----------------------------------
	AGV_CONTAINER_DELIVERED 		(EventSeverity.NORMAL,		AgvTask.class),
	
	//------------------------------------- AGV Errors --------------------------------------
	AGV_FORKLIFT_DAMAGED 			(EventSeverity.ERROR,		Forklift.class),
	AGV_FORKLIFT_COLLISION 			(EventSeverity.GLOBAL_EROR,	Forklift.class, Forklift.class),
	
	
	//------------------------------- Monitoring Notifications ------------------------------
	
	//---------------------------------- Monitoring Errors ----------------------------------
	MONITORING_HANDLE_EVENT_FAILED	(EventSeverity.GLOBAL_EROR)
	
	;
	
	public enum EventSeverity{
		/** a non important info */
		INFO, 
		/** a normal event, e.g. task finished */
		NORMAL,
		/** should be handled with priority to the normal event, e.g. Conveyor - lack of lubricant */
		IMPORTANT,
		/** an error which makes human interaction necessary, immediate stop of subsystem required */
		ERROR,
		/** global errors which should immediately stop all subsystems, e.g. factory is burning */
		GLOBAL_EROR;
	}
	
	public Class<?>[] attachmentTypes;
	public EventSeverity severity;
	
	private EventKind(EventSeverity severity, Class<?>... attachmentTypes) {
		this.attachmentTypes = attachmentTypes;
		this.severity = severity;
	}
	
	@Override
	public String toString() {
		StringBuilder attachmentsSb = new StringBuilder();
		for (int i = 0; i < attachmentTypes.length; i++) {
			if (i != 0)
				attachmentsSb.append(", ");
			attachmentsSb.append(attachmentTypes[i].getSimpleName());
		}
		return String.format("(Event: \"%s\", Attachments: %d -- [%s])", super.toString(), attachmentTypes.length, attachmentsSb.toString());
	}
	
	
}
