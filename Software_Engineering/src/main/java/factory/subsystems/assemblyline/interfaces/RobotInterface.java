package factory.subsystems.assemblyline.interfaces;

import factory.shared.enums.Material;
import factory.shared.interfaces.ContainerDemander;
import factory.subsystems.assemblyline.RobotType;

public interface RobotInterface extends ContainerDemander {

	RobotType getRobotType();
	
	/**
	 * @return the amount of Materials the Robot has left
	 */
	int getMaterialAmount();
	
	Material getMaterialType();
	
	boolean isReady();
	
	/**
	 * Makes the robot do work. Only call when isReady() == true.
	 */
	void start();
	
}
