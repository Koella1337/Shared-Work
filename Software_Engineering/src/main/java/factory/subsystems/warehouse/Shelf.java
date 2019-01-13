package factory.subsystems.warehouse;

import java.awt.Color;
import java.awt.Graphics;

import factory.shared.Position;
import factory.shared.interfaces.Placeable;

public class Shelf implements Placeable {

	private final Position pos;
	
	public Shelf(Position position) {
		super();
		this.pos = position;
	}

	@Override
	public Position getPosition() {
		return pos;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.DARK_GRAY);	//TODO
		g.drawRect(0, 0, pos.xSize, pos.ySize);
	}

}
