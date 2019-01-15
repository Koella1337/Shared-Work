package factory.subsystems.assemblyline;

import factory.shared.Position;

public class Car {
	
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
	
	public RobotTypes infront() {
		switch(pos) {
		case 0:
			return RobotTypes.GRABBER;
		case 1:
			return RobotTypes.SCREWDRIVER;
		case 2:
			return RobotTypes.PAINTER;
		case 3:
			return RobotTypes.INSPECTOR;
		default:
			return null;
		}
	}
	
	public void move() {
		pos++;
	}

}
