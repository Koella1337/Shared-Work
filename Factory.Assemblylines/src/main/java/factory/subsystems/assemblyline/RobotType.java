package factory.subsystems.assemblyline;

public enum RobotType {
	GRABBER			("Grabber", 1), 
	SCREWDRIVER		("Screwdriver", 5), 
	PAINTER			("Painter", 3), 
	INSPECTOR		("Inspector", 0);
	
	/** Humanly readable name of the RobotType. */
	public final String displayName;
	
	/** How many materials this RobotType uses every time it does work. */
	public final int materialsUsed;

	private RobotType(String displayName, int materialsUsed) {
		this.displayName = displayName;
		this.materialsUsed = materialsUsed;
	}
	
	@Override
	public String toString() {
		return displayName;
	}
}
