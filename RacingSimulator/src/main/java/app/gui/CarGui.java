package app.gui;

import java.awt.Color;
import java.awt.Graphics;

import app.model.SimulationConstants;
import app.model.car.Car;

public class CarGui {

	private Car car;

	public CarGui(Car car) {
		super();
		this.car = car;
	}

	public void drawSelectedCar(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		int yPos = (int) car.getTransform().getYPos();
		g.fillRect(0, yPos-4, SimulationConstants.TRACK_WIDTH, (int) (car.getTransform().getYSize()+8));
		drawUnselectedCar(g) ;
	}

	/**
	 * draws the {@link Car} on the {@link Graphics} object
	 * 
	 * @param g the graphics object to draw on
	 */
	public void drawUnselectedCar(Graphics g) {
		int xPos = (int) this.car.getTransform().getXPos();
		int yPos = (int) this.car.getTransform().getYPos();
		int xSize = (int) this.car.getTransform().getXSize();
		int ySize = (int) this.car.getTransform().getYSize();
		
		g.setColor(Color.BLACK);
		g.drawString(""+this.car.getId(), 5, (int) (yPos+ySize));

		g.setColor(car.getColor());
		g.fillRect(xPos, yPos, xSize, ySize);

		g.setColor(Color.BLACK);
		g.drawRect(xPos, yPos, xSize, ySize);
	}
}
