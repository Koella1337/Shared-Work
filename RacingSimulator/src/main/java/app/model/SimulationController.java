package app.model;

import java.util.Set;

import app.model.car.Car;

public interface SimulationController {

	void startSimulation();
	
	void resetSimulation();
	
	SimulationStatus getStatus();
	
	Set<? extends Car> getCars();
	
}
