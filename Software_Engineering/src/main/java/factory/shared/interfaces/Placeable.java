package factory.shared.interfaces;

import java.awt.Graphics;

import factory.shared.Position;

public interface Placeable {

	Position getPosition();

	/**
	 * draws the {@link Placeable} on the {@link Graphics} object
	 * 
	 * @param g the graphics object to draw on
	 */
	void draw(Graphics g);
}
