package factory.subsystems.assemblyline.interfaces;

import factory.shared.enums.SubsystemStatus;

public interface RobotInterface{
		
	int getMaterials();
	
	SubsystemStatus status();

	void start();
	
}