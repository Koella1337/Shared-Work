package app.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import factory.shared.AbstractSubsystem;
import factory.shared.interfaces.Placeable;

class FactoryPanel extends GUIPanel {

	private List<AbstractSubsystem> subsystems;

	public FactoryPanel(int fps) {
		super(fps);
		this.subsystems = new ArrayList<>();
		this.setBackground(Color.WHITE);
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

		g.translate(posX, posY);
		placeable.draw(g);
		g.translate(-posX, -posY);
	}

	public void addSubsystemToPanel(AbstractSubsystem subsystem) {
		this.subsystems.add(subsystem);
	}

}
