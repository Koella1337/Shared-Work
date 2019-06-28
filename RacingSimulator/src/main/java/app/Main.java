package app;

import app.gui.GuiHandler;
import app.model.Simulation;
import app.model.SimulationController;

public class Main {

	public static void main(String[] args) {
		SimulationController simulation = new Simulation();
		new GuiHandler(simulation);
	}

}
