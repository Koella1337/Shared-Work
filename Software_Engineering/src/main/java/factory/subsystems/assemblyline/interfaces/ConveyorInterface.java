package factory.subsystems.assemblyline.interfaces;

import factory.shared.interfaces.Placeable;

public interface ConveyorInterface extends Placeable {

	void setSpeed(double speed);
	
	double getSpeed();

	void start();
	
}
