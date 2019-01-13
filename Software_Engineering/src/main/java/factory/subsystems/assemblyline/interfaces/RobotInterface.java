package factory.subsystems.assemblyline.interfaces;

import factory.shared.Container;

public interface RobotInterface{
	
	/**
	 * adds a box to the robot
	 * @param box
	 */
	void addBox(Container box);
	
	/**
	 * does the robot-specific work
	 */
	void doWork();
	
	/**
	 * @return true if the robot has finished its work and is ready again
	 */
	boolean isReady();
	
	/**
	 * notifies the monitoring system about the given event 
	 * @param task
	 * @param event
	 */
	//void notifyMonitoringSystem(Task task, RobotEvent event);
	
	int getMaterials();
	
}