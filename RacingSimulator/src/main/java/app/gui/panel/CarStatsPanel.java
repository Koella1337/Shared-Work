package app.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import app.model.car.Car;
import app.model.car.CarUtils;

@SuppressWarnings("serial")
public class CarStatsPanel extends JPanel {

	private Car currentCar;

	public CarStatsPanel() {
		setBackground(Color.LIGHT_GRAY);
		setPreferredSize(new Dimension(getWidth(), 700));
	}

	public void showCarStats(Car car) {
		this.currentCar = car;
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (currentCar != null) {
			String posString = String.format("Position: (%4.0f, %4.0f)", currentCar.getTransform().getXPos(),
					currentCar.getTransform().getYPos());
			int xPosOfStatValues = 6;
			int yPosOfStatValues  = 20;
			int spaceBetweenStatValue = 40;
			
			g.drawString(posString, xPosOfStatValues, yPosOfStatValues);
			yPosOfStatValues+=spaceBetweenStatValue;

			String speedString = String.format("Speed: %6.2f", currentCar.getCurrentSpeed());
			g.drawString(speedString,xPosOfStatValues,yPosOfStatValues);
			yPosOfStatValues+=spaceBetweenStatValue;
			
			drawPoints(g, xPosOfStatValues, yPosOfStatValues, "maxSpeed",  currentCar.getMaxSpeed().getPoints(), CarUtils.CAR_STAT_MAX_POINTS);
			yPosOfStatValues+=spaceBetweenStatValue;
			
			drawPoints(g, xPosOfStatValues, yPosOfStatValues, "acceleration", currentCar.getAcceleration().getPoints(), CarUtils.CAR_STAT_MAX_POINTS);
			yPosOfStatValues+=spaceBetweenStatValue;
			
			drawPoints(g, xPosOfStatValues, yPosOfStatValues, "stability",  currentCar.getStability().getPoints(), CarUtils.CAR_STAT_MAX_POINTS);
			yPosOfStatValues+=spaceBetweenStatValue;
		}
	}
	
	private void drawPoints(Graphics g, int x, int y, String label, int points, int maxPoints) {
		g.drawString(label, x, y);
		for (int i = 0; i < maxPoints; i++) {
			g.setColor(Color.BLACK);
			int pointSize = 12;
			if (points > i) {
				g.fillOval(x + i * pointSize, y + 4, pointSize, pointSize);
			} else {
				g.drawOval(x + i * pointSize, y + 4, pointSize, pointSize);
			}
		}
	}

}