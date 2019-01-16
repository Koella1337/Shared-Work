package factory.shared.enums;

public enum Material {
	BODIES				(20),
	WHEELS				(30),
	SCREWS				(50),
	LUBRICANT			(15),
	
	COLOR_RED			(15),
	COLOR_GREEN			(15),
	COLOR_BLUE			(15),
	COLOR_WHITE			(15),
	COLOR_BLACK			(15),
	COLOR_GRAY			(15),
	
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
}
