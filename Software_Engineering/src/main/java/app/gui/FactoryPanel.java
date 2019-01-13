package app.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import factory.shared.AbstractSubsystem;
import factory.shared.interfaces.Placeable;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

class FactoryPanel extends GUIPanel {

	private MonitoringInterface monitor;
	private List<AbstractSubsystem> subsystems;

	public FactoryPanel(int fps, MonitoringInterface monitor) {
		super(fps);
		this.subsystems = new ArrayList<>();
		this.setBackground(Color.WHITE);
		this.monitor = monitor;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		this.subsystems.forEach(s -> {
			s.getPlaceables().forEach(pl -> drawPlaceableOnGraphics(g, pl));
		});
	}

	private void drawPlaceableOnGraphics(Graphics g, Placeable placeable) {
		int posX = placeable.getPosition().xPos;
		int posY = placeable.getPosition().yPos;
		
		g.drawString("shipping box", this.monitor.getShippingBox().getPosition().xPos,this.monitor.getShippingBox().getPosition().yPos);
		
		g.translate(posX, posY);
		placeable.draw(g);
		g.translate(-posX, -posY);
	}

	public void addSubsystemToPanel(AbstractSubsystem subsystem) {
		this.subsystems.add(subsystem);
	}

}
