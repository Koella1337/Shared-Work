package factory.subsystems.warehouse;

import java.awt.Graphics;

import factory.shared.Position;
import factory.shared.interfaces.Placeable;

public class Shelf implements Placeable {

	private final Position position;
	
	public Shelf(Position position) {
		super();
		this.position = position;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
