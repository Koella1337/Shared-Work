package factory.app.gui;

import java.awt.Color;
import java.awt.Graphics;

import factory.subsystems.monitoring.interfaces.MonitoringInterface;

@SuppressWarnings("serial")
class MenuPanel extends GUIPanel {
	
	public MenuPanel(int fps, MonitoringInterface monitor) {
		super(fps);
		this.setBackground(Color.LIGHT_GRAY);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
	
}
