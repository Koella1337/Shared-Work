package factory.app.gui;

import factory.shared.AbstractSubsystem;
import factory.shared.interfaces.GUIHandler;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public class GUIHandlerImpl implements GUIHandler {

	private static final int FPS = 30;
	
	private UserInterface ui;

	public GUIHandlerImpl(MonitoringInterface monitor, UIConfiguration uiConfig) {
		super();
		this.ui = new UserInterface(FPS, monitor, uiConfig);
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
	
}
