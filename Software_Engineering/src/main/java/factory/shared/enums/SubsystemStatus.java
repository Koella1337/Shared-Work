package factory.shared.enums;

/**
 * The status of a subsystem
 */
public enum SubsystemStatus {
	/**
	 * running, everything working without major problems
	 */
	RUNNING,
	
	/**
	 * the subsystem is ready to do something, but some precondition isn't met
	 */
	WAITING,
	
	/**
	 * the subsystem is stopped 
	 */
	STOPPED,
	
	/**
	 * the subsystem is broken and must be repaired
	 */
	BROKEN;
}
