package factory.shared;

/**
 * this class is used as general Position of an Object in the factory
 */
public class Position {

	public int xPos, yPos;
	public int xSize, ySize;
	
	public Position(int xPos, int yPos) {
		this(xPos, yPos, 1, 1);
	}
	
	public Position(int xPos, int yPos, int xSize, int ySize) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.xSize = xSize;
		this.ySize = ySize;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Position)) {
			return false;
		}
		Position pos2 = (Position) other;
		
		return xPos == pos2.xPos && yPos == pos2.yPos && xSize == pos2.xSize && ySize == pos2.ySize;
	}
	
	@Override
	public String toString() {
		return String.format("(Pos: %3d, %3d | Size: %3d, %3d)", xPos, yPos, xSize, ySize);
	}
	
	//----------------------------------------------------- STATIC METHODS -----------------------------------------------------
	
	public static Position addPosition(Position pos1, Position pos2) {
		return new Position(pos1.xPos + pos2.xPos, pos1.yPos + pos2.yPos);
	}
	
	public static Position subtractPosition(Position pos1, Position pos2) {
		return new Position(pos1.xPos - pos2.xPos, pos1.yPos - pos2.yPos);
	}
	
	public static Position multiply(Position pos, int factor) {
		return new Position(pos.xPos * factor, pos.yPos * factor);
	}
	
	public static Position divide(Position pos, int div) {
		return new Position(pos.xPos / div, pos.yPos / div);
	}
	
	/**
	 * @return Euclidean distance of Position (xPos and yPos)
	 */
	public static double length(Position pos) {
		return Math.sqrt(pos.xPos * pos.xPos + pos.yPos * pos.yPos);
	}
	

}
