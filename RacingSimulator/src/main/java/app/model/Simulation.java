package app.model;

import static app.model.SimulationConstants.SIMULATION_UPDATES_PER_SECOND;
import static app.model.SimulationStatus.NOT_READY;
import static app.model.SimulationStatus.READY;
import static app.model.SimulationStatus.RUNNING;

import java.util.Set;

import app.model.car.Car;
import app.timer.UpdateTimer;
import app.timer.Updateable;

public class Simulation implements SimulationController, Updateable {

	private final UpdateTimer timer;
	
	private SimulationRound round;
	private SimulationStatus status;
	
	public Simulation() {
		status = NOT_READY;
		timer = new UpdateTimer(this, SIMULATION_UPDATES_PER_SECOND);
		round = new SimulationRound(null);
		status = READY;
	}
	
	@Override
	public void startSimulation() {
		timer.start();
		status = RUNNING;
	}

	@Override
	public void resetSimulation() {
		status = NOT_READY;
		timer.stop();
		round = new SimulationRound(null);
		status = READY;
	}
	
	@Override
	public SimulationStatus getStatus() {
		return status;
	}

	@Override
	public Set<? extends Car> getCars() {
		return round.getCars();
	}

	@Override
	public void update() {
		round.update();
		if (round.isFinished()) {
			round = new SimulationRound(round.getPlacements());
		}
	}	
	
}
