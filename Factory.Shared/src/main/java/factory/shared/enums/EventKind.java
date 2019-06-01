package factory.shared.enums;

import factory.shared.Task;
import factory.shared.ResourceBox;
import factory.shared.Transaction;

import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.ContainerSupplier;
import factory.shared.interfaces.ContainerDemander;

/**
 * The kind of a FactoryEvent.<br>
 * EventKinds can portray simple Notifications (e.g. a Task being completed), but can also be errors.
 */
public enum EventKind {
	//----------------------------------- Generic Events -----------------------------------
	TASK_NOT_COMPLETED_BEFORE_DEADLINE (EventSeverity.INFO,		Task.class),
	
	RESOURCEBOX_FULL				(EventSeverity.ERROR, 		ResourceBox.class),
	RESOURCEBOX_ALMOST_FULL			(EventSeverity.IMPORTANT, 	ResourceBox.class),
	
	//------------------------------- AssemblyLine Notifications -------------------------------
	CAR_FINISHED 					(EventSeverity.NORMAL, 		Material.class, ContainerSupplier.class),
	LACK_OF_MATERIAL 				(EventSeverity.IMPORTANT,	Material.class, ContainerDemander.class),
	
	//---------------------------------- AssemblyLine Errors -----------------------------------
	ROBOT_BROKEN					(EventSeverity.IMPORTANT, 	ContainerDemander.class),
	CONVEYOR_BROKEN					(EventSeverity.ERROR, 		Placeable.class),
	
	//------------------------------- Warehouse Notifications -------------------------------
	WAREHOUSE_TASK_COMPLETED		(EventSeverity.NORMAL,		Task.class, ContainerSupplier.class),
	WAREHOUSE_NEW_TRANSACTION		(EventSeverity.NORMAL,		Transaction.class),	
	
	//---------------------------------- AGV Notifications ----------------------------------
	AGV_CONTAINER_DELIVERED 		(EventSeverity.NORMAL,		Task.class),
	
	//------------------------------------- AGV Errors --------------------------------------
	AGV_FORKLIFT_DAMAGED 			(EventSeverity.ERROR,		Placeable.class),
	AGV_FORKLIFT_COLLISION 			(EventSeverity.GLOBAL_EROR,	Placeable.class, Placeable.class),
	AGV_PATHING_IMPOSSIBLE			(EventSeverity.IMPORTANT,	Task.class),
	
	
	//------------------------------- Monitoring Notifications ------------------------------
	
	//---------------------------------- Monitoring Errors ----------------------------------
	MONITORING_HANDLE_EVENT_FAILED	(EventSeverity.GLOBAL_EROR)
	
	;
	
	public enum EventSeverity {
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
