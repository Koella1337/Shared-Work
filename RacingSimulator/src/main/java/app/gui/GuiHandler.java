package app.gui;

import java.util.List;

import app.gui.panel.MenuPanel;
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
		this.appWindow = new AppWindow(this,SimulationConstants.TRACK_WIDTH,SimulationConstants.TRACK_HEIGHT);
		this.update();
	}
	
	public void startSimulation() {
		System.out.println("GuiHandler.startSimulation");
		this.simulation.startSimulation();
		this.updateTimer.start();
	}
	
	public void resetSimulation() {
		System.out.println("GuiHandler.resetSimulation");
		this.simulation.resetSimulation();
		this.updateTimer.stop();
		update();
	}

	@Override
	public void update() {
		List<? extends Car> carList = simulation.getCars();
		this.appWindow.setCars(carList);
		this.appWindow.repaint();
		this.appWindow.getMenuPanel().repaint();
	}

	public Simulation getSimulation() {
		return simulation;
	}
	
	public List<? extends Car> getCars(){
		return this.appWindow.getSimulationPanel().getCarList();
	}

	public void showStatsForCar(Car car) {
		this.appWindow.getMenuPanel().showCarStats(car);
		this.appWindow.getSimulationPanel().setSelectedCar(car);
	}

	public MenuPanel getMenuPanel() {
		return this.appWindow.getMenuPanel();
	}

}
