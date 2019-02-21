package app.gui;

import factory.shared.AbstractSubsystem;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public class GUIHandler implements Stoppable {

	private static final int FPS = 20;
	
	private MonitoringInterface monitor;
	private UserInterface ui;

	public GUIHandler(MonitoringInterface monitor, UIConfiguration uiConfig) {
		super();
		this.monitor = monitor;
		this.ui = new UserInterface(FPS, this.monitor,uiConfig);
	}

	@Override
	public void start() {
		this.ui.start();
	}

	@Override
	public void stop() {
		this.ui.stop();
	}
	
	public void addToFactoryPanel(AbstractSubsystem subsystem) {
		this.ui.getFactoryPanel().addSubsystemToPanel(subsystem);
	}

	public void setMenuPanel(MenuPanel menuPanel) {
		this.ui.setMenuPanel(menuPanel);
	}
	
	public void setCurrentSubsystem(AbstractSubsystem subsystem) {
		this.ui.getMenuPanel().setCurrentSubSystem(subsystem);
	}
	
	
}
