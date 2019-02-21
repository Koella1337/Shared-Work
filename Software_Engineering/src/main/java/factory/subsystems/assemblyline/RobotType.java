package factory.subsystems.assemblyline;

public enum RobotType {
	GRABBER			("Grabber"), 
	SCREWDRIVER		("Screwdriver"), 
	PAINTER			("Painter"), 
	INSPECTOR		("Inspector");
	
	public final String displayName;

	private RobotType(String displayName) {
		this.displayName = displayName;
	}
}
