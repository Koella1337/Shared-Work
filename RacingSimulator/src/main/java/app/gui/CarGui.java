package app.gui;

import java.awt.Color;
import java.awt.Graphics;

import app.model.car.Car;

public class CarGui {

	private Car car;

	public CarGui(Car car) {
		super();
		this.car = car;
	}

	/**
	 * draws the {@link Car} on the {@link Graphics} object
	 * 
	 * @param g the graphics object to draw on
	 */
	public void draw(Graphics g) {
		int xPos = (int) this.car.getTransform().getXPos();
		int yPos = (int) this.car.getTransform().getYPos();
		int xSize = (int) this.car.getTransform().getXSize();
		int ySize= (int) this.car.getTransform().getYSize();

		g.setColor(Color.GREEN);
		g.fillRect(xPos,yPos,xSize,ySize);
		
		g.setColor(Color.BLACK);
		g.drawRect(xPos,yPos,xSize,ySize);
	}

}
