package app.gui;

import java.awt.Color;
import java.awt.Graphics;

import factory.shared.AbstractSubsystem;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

@SuppressWarnings("serial")
class MenuPanel extends GUIPanel {
	
	private SubsystemMenu subsystemMenu;
	
	public MenuPanel(int fps, MonitoringInterface monitor) {
		super(fps);
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
