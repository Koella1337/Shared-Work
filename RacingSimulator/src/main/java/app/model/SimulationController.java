package app.model;

import java.util.List;

import app.model.car.Car;

public interface SimulationController {

	void startSimulation();
	
	void pauseSimulation();
	
	void resetSimulation();
	
	SimulationStatus getStatus();
	
	List<? extends Car> getCars();
	
}
