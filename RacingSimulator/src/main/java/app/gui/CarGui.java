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
		draw(g, car.getColor());
	}

	public void drawSelected(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		int yPos = (int) car.getTransform().getYPos();
		g.fillRect(0, yPos, 1000, (int) (car.getTransform().getYSize()+1));
		draw(g, car.getColor());
	}

	public void draw(Graphics g, Color color) {
		int xPos = (int) this.car.getTransform().getXPos();
		int yPos = (int) this.car.getTransform().getYPos();
		int xSize = (int) this.car.getTransform().getXSize();
		int ySize = (int) this.car.getTransform().getYSize();

		g.setColor(color);
		g.fillRect(xPos, yPos, xSize, ySize);

		g.setColor(Color.BLACK);
		g.drawRect(xPos, yPos, xSize, ySize);
	}
}
