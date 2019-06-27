package app.gui;

import java.util.List;

import app.model.Simulation;
import app.model.SimulationConstants;
import app.model.car.Car;
import app.timer.UpdateTimer;
import app.timer.Updateable;

public class GuiHandler implements Updateable {

	private static final int REFRESH_RATE = 30;

	private AppWindow appWindow;
	
	private Simulation simulation;
	private UpdateTimer updateTimer;

	public GuiHandler(Simulation simulation) {
		super();
		this.simulation = simulation;
		this.updateTimer = new UpdateTimer(this, REFRESH_RATE);
		this.appWindow = new AppWindow(SimulationConstants.TRACK_WIDTH,SimulationConstants.TRACK_HEIGHT);
	}
	
	public void startSimulation() {
		this.simulation.startSimulation();
		this.updateTimer.start();
	}
	
	public void resetSimulation() {
		this.simulation.resetSimulation();
	}

	@Override
	public void update() {
		List<? extends Car> carList = simulation.getCars();

		
		
		
	}

}
