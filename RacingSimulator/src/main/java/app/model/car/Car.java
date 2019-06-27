package app.model.car;

import java.util.Random;

import app.model.Transform;

public class Car {
	
	private int xPos = new Random().nextInt(1000);//TODO remove
	private int yPos = new Random().nextInt(1000);//TODO remove

	public static Car createRandomCar() {
		
		
		return null;
	}
	
	public Transform getTransform() {
		return new Transform(xPos, yPos, 16.0, 10.0);
	}
	
}
