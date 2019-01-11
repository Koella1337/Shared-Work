package factory.shared.interfaces;

import java.awt.Graphics;

import factory.shared.Position;

public interface Placeable {

	Position getPosition();
	
	void draw(Graphics g);	
	
}
