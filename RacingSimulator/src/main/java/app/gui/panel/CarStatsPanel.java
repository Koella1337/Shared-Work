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
			g.drawString(posString, 6, 20);

			String speedString = String.format("Speed: %6.2f", currentCar.getCurrentSpeed());
			g.drawString(speedString,6,40);
			
			drawPoints(g, 6, 80, "maxSpeed",  currentCar.getMaxSpeed().getPoints(), CarUtils.CAR_STAT_MAX_POINTS);
			drawPoints(g, 6, 120, "acceleration", currentCar.getAcceleration().getPoints(), CarUtils.CAR_STAT_MAX_POINTS);
			drawPoints(g, 6, 160, "stability",  currentCar.getStability().getPoints(), CarUtils.CAR_STAT_MAX_POINTS);
		}
	}

	private void drawPoints(Graphics g, int x, int y, String label, int points, int maxPoints) {
		g.drawString(label, x, y);
		for (int i = 0; i < maxPoints; i++) {
			g.setColor(Color.BLACK);
			if (points > i) {
				g.fillOval(x + i * 12, y + 4, 12, 12);
			} else {
				g.drawOval(x + i * 12, y + 4, 12, 12);
			}
		}
	}

}