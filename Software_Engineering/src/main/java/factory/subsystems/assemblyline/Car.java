package factory.subsystems.assemblyline;

import java.awt.Color;
import java.awt.Graphics;

import factory.shared.Position;
import factory.shared.interfaces.Placeable;

public class Car implements Placeable{
	
	private int pos;
	private Position position;
	
	public Car(Position position) {
		this.position = position;
		pos = 0;
	}
	
	public void setPosition(Position p) {
		position = p;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public RobotType infront() {
		switch(pos) {
		case 0:
			return RobotType.GRABBER;
		case 1:
			return RobotType.SCREWDRIVER;
		case 2:
			return RobotType.PAINTER;
		case 3:
			return RobotType.INSPECTOR;
		default:
			return null;
		}
	}
	
	public void move() {
		pos++;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.MAGENTA);
		g.fillRect(position.xPos, position.yPos, position.xSize, position.ySize);
	}

}
