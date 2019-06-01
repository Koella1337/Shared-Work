package factory.app.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import factory.shared.AbstractSubsystem;
import factory.shared.Constants;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.interfaces.Placeable;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

@SuppressWarnings("serial")
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

		drawStaffQuarter(g);
		drawShippingBox(g);
		
		this.subsystems.forEach(s -> {
			s.getPlaceables().forEach(pl -> drawPlaceableOnGraphics(g, pl));
		});
	}

	private void drawStaffQuarter(Graphics g) {
		Position pos = monitor.getStaffQuarterPosition();
		
		g.setColor(Color.PINK);
		g.fillRect(pos.xPos + 1, pos.yPos + 1, pos.xSize - 1, pos.ySize - 1);
		g.setColor(Constants.UI_BORDER_COLOR);
		g.drawRect(pos.xPos, pos.yPos, pos.xSize, pos.ySize);
		g.drawString("Staff Quarters", pos.xPos + 10, pos.yPos + 20);
	}

	private void drawShippingBox(Graphics g) {
		ResourceBox box = monitor.getShippingBox();
		Position pos = box.getPosition();
		
		drawPlaceableOnGraphics(g, box);
		g.drawString("Shipping Box", pos.xPos + 10, pos.yPos + 20);
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
