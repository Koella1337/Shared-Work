package factory.subsystems.assemblyline.interfaces;

import java.util.List;

import factory.shared.Container;
import factory.shared.Task;
import factory.shared.enums.SubsystemStatus;
import factory.subsystems.assemblyline.RobotEvent;

public interface RobotInterface{
	
	/**
	 * adds a box to the robot
	 * @param box
	 */
	void addBox(Container box);
	
	int getMaterials();
	
	SubsystemStatus status();
	
}