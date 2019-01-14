package factory.shared;

/**
 * this class is used as general Position of an Object in the factory
 */
public class Position implements Cloneable {

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
	
	@Override
	public Position clone() {
		return new Position(this.xPos, this.yPos, this.xSize, this.ySize);
	}
	
	//----------------------------------------------------- STATIC METHODS -----------------------------------------------------
	
	/** Makes a new Position from the supplied pos and adds x and y respectively. */
	public static Position add(Position pos, int x, int y) {
		return new Position(pos.xPos + x, pos.yPos + y);
	}
	
	public static Position addPosition(Position pos1, Position pos2) {
		return add(pos1, pos2.xPos, pos2.yPos);
	}
	
	/** Makes a new Position from the supplied pos and subtracts x and y respectively. */
	public static Position subtract(Position pos, int x, int y) {
		return new Position(pos.xPos - x, pos.yPos - y);
	}
	
	public static Position subtractPosition(Position pos1, Position pos2) {
		return subtract(pos1, pos2.xPos, pos2.yPos);
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
	
	/** Checks if the first position is overlapping with the second position. */
	public static boolean isOverlapping(Position p1, Position p2) {
		if (isLeftOf(p1, p2) || isRightOf(p1, p2) || isAbove(p1, p2) || isBelow(p1, p2))
			return false;
		else
			return true;
	}
	
	/** Checks if leftpos is to the left of rightPos without overlapping sizes */
	public static boolean isLeftOf(Position leftPos, Position rightPos) {
		if ( (leftPos.xPos + leftPos.xSize) < rightPos.xPos )
			return true;
		else
			return false;
	}
	
	/** Checks if rightPos is to the right of leftPos without overlapping sizes */
	public static boolean isRightOf(Position rightPos, Position leftPos) {
		if ( rightPos.xPos > (leftPos.xPos + leftPos.xSize) )
			return true;
		else
			return false;
	}
	
	/** Checks if abovePos is above belowPos without overlapping sizes */
	public static boolean isAbove(Position abovePos, Position belowPos) {
		if ( (abovePos.yPos + abovePos.ySize) < belowPos.yPos )
			return true;
		else
			return false;
	}

	/** Checks if belowPos is below abovePos without overlapping sizes */
	public static boolean isBelow(Position belowPos, Position abovePos) {
		if ( belowPos.yPos > (abovePos.yPos + abovePos.ySize) )
			return true;
		else
			return false;
	}
	
}
