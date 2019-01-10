package factory.shared.enums;

import factory.shared.Task;

/**
 * The kind of a FactoryEvent.<br>
 * EventKinds can portray simple Notifications (e.g. a Task being completed), but can also be errors.
 */

public enum EventKind {
	//------------------------------- RobotArms Notifications -------------------------------
	
	//---------------------------------- RobotArms Errors -----------------------------------
	
	
	//------------------------------- Conveyors Notifications -------------------------------
	CONVEYORS_LACK_OF_OIL			(EventSeverity.IMPORTANT),
	
	//---------------------------------- Conveyors Errors -----------------------------------
	
	
	
	//------------------------------- Warehouse Notifications -------------------------------
	
	//---------------------------------- Warehouse Errors -----------------------------------
	
	
	
	//---------------------------------- AGV Notifications ----------------------------------
	AGV_CONTAINER_DELIVERED 		(EventSeverity.NORMAL,Task.class),
	
	//------------------------------------- AGV Errors --------------------------------------
	AGV_FORKLIFT_DAMAGED 		(EventSeverity.ERROR),
	AGV_FORKLIFT_COLLISION 		(EventSeverity.ERROR),
	
	
	//------------------------------- Monitoring Notifications ------------------------------
	
	//---------------------------------- Monitoring Errors ----------------------------------
	MONITORING_HANDLE_EVENT_FAILED(EventSeverity.GLOBAL_EROR)
	
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
			attachmentsSb.append(attachmentTypes[i].getSimpleName());
			if (i != attachmentTypes.length - 1)
				attachmentsSb.append(", ");
		}
		return String.format("Event: \"%s\", Attachments: %d -- [%s]", super.toString(), attachmentTypes.length, attachmentsSb.toString());
	}
	
	
}
