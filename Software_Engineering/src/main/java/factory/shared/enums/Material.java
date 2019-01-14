package factory.shared.enums;

public enum Material {
	CAR_BODIES			(200),
	WHEELS				(300),
	SCREWS				(500),
	LUBRICANT			(100),
	COLOR_RED			(300),
	COLOR_GREEN			(300),
	COLOR_BLUE			(300),
	COLOR_WHITE			(300),
	COLOR_BLACK			(300),
	COLOR_GRAY			(300),
	CAR (1);
	
	
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
