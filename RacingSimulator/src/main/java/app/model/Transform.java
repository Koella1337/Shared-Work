package app.model;

public class Transform implements Cloneable{
	
	private double xPos;
	private double yPos;
	
	private double xSize;
	private double ySize;
	
	public Transform() {
		this(0, 0, 0, 0);
	}
	
	public Transform(double xPos, double yPos, double xSize, double ySize) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.xSize = xSize;
		this.ySize = ySize;
	}
	
	@Override
	protected Transform clone() {
		return new Transform(xPos, yPos, xSize, ySize);
	}
	
	@Override
	public String toString() {
		return String.format("[Transform: xPos=%.2f | yPos=%.2f | xSize=%.2f | ySize=%.2f]", xPos, yPos, xSize, ySize);
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

	public double getXSize() {
		return xSize;
	}

	public void setXSize(double xSize) {
		this.xSize = xSize;
	}

	public double getYSize() {
		return ySize;
	}

	public void setYSize(double ySize) {
		this.ySize = ySize;
	}
	
}
