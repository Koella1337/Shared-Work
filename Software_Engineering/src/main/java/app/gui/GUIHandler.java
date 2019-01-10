package app.gui;

import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.AbstractSubsystem;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public class GUIHandler implements Stoppable{

	private static final int FPS = 30;
	
	private MonitoringInterface monitor;
	private UserInterface ui;

	public GUIHandler(MonitoringInterface monitor) {
		super();
		this.monitor = monitor;
		this.ui = new UserInterface(FPS, this.monitor);
	}

	public void start() {
		this.ui.start();
	}

	public void stop() {
		this.ui.stop();
	}
	
	public void addToFactoryPanel(Placeable placeable) {
		this.ui.getFactoryPanel().getObjectsToDraw().add(placeable);
	}

	public void setMenuPanel(MenuPanel menuPanel) {
		this.ui.setMenuPanel(menuPanel);
	}
	
	public void setCurrentSubsystem(AbstractSubsystem subsystem) {
		this.ui.getMenuPanel().setCurrentSubSystem(subsystem);
	}
	
}
