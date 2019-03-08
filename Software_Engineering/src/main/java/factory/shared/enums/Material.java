package factory.shared.enums;

import java.awt.Color;

public enum Material {
	BODIES				(15),
	WHEELS				(20),
	SCREWS				(100),
	LUBRICANT			(200),
	
	COLOR_RED			(50),
	COLOR_GREEN			(50),
	COLOR_BLUE			(50),
	COLOR_WHITE			(50),
	COLOR_BLACK			(50),
	COLOR_GRAY			(50),
	
	CAR_RED 			(1),
	CAR_GREEN 			(1),
	CAR_BLUE 			(1),
	CAR_WHITE 			(1),
	CAR_BLACK 			(1),
	CAR_GRAY 			(1);
	
	
	/** Amount of the material that fits into a single container. */
	public final int containerAmount;
	
	private Material(int containerAmount) {
		this.containerAmount = containerAmount;
	}
	
	@Override
	public String toString() {
		return this.name();
	}
	
	/**
	 * @return the corresponding java.awt.Color if this Material is a color. <b>null</b> otherwise.
	 */
	public Color toColor() {
		switch(this) {
			case COLOR_BLACK: 	return Color.BLACK;
			case COLOR_GRAY: 	return Color.GRAY;
			case COLOR_RED: 	return Color.RED;
			case COLOR_GREEN: 	return Color.GREEN;
			case COLOR_BLUE:	return Color.BLUE;
			case COLOR_WHITE: 	return Color.WHITE;
			default:
				return null;
		}
	}
}
