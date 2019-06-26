package app.model;

public class Position {
	
	private double xPos;
	private double yPos;
	
	public Position(double xPos, double yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public double getXPos() {
		return xPos;
	}

	public void setXPos(double xPos) {
		this.xPos = xPos;
	}

	public double getYPos() {
		return yPos;
	}

	public void setYPos(double yPos) {
		this.yPos = yPos;
	}
	
}
