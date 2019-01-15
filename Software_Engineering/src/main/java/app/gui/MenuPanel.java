package app.gui;

import java.awt.Color;
import java.awt.Graphics;

import factory.shared.AbstractSubsystem;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

class MenuPanel extends GUIPanel {
	
	private MonitoringInterface monitor;
	private SubsystemMenu subsystemMenu;

	
	public MenuPanel(int fps, MonitoringInterface monitor) {
		super(fps);
		this.monitor = monitor;
		this.setBackground(Color.LIGHT_GRAY);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public void setCurrentSubSystem(AbstractSubsystem currentSubSystem) {
		if(this.subsystemMenu != null) {
			this.remove(this.subsystemMenu);
		}
		this.subsystemMenu = currentSubSystem.getCurrentSubsystemMenu();
		this.add(subsystemMenu);
	}
	
}
