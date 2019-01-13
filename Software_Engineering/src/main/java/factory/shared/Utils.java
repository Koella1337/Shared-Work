package factory.shared;

import java.util.Arrays;
import java.util.Objects;

import org.w3c.dom.Element;

import factory.shared.Constants.PlaceableSize;

/**
 * Some static utility functions.
 */
public class Utils {

	/**
	 * Reads the position and size sub-elements of the supplied xmlElem and returns them as a Position object.
	 * @param xmlElem - an XML Element that contains at least a "position" and possibly a "size" sub-element
	 * @return a Position object
	 */
	public static Position getPositionFromXmlElement(Element xmlElem) {
		Objects.requireNonNull(xmlElem);
		
		Element positionElem = (Element) xmlElem.getElementsByTagName("position").item(0);
		Element sizeElem = (Element) xmlElem.getElementsByTagName("size").item(0);
		
		return parsePosition(positionElem.getTextContent(), sizeElem == null ? null : sizeElem.getTextContent());
	}
	
	/**
	 * Assigns the xSize and ySize of a Position to the respective x, y values of the given PlaceableSize.
	 */
	public static Position assignSize(Position pos, PlaceableSize size) {
		Objects.requireNonNull(pos);
		Objects.requireNonNull(size);
		
		pos.xSize = size.x;
		pos.ySize = size.y;
		return pos;
	}
	
	/**
	 * Takes a Position string and (optionally) a Size string and parses a Position object from them.
	 */
	public static Position parsePosition(String posString, String sizeString) {
		Objects.requireNonNull(posString);
		
		int[] pos = Arrays.stream(posString.split(",")).mapToInt(Integer::parseInt).toArray();
		Position retVal = new Position(pos[0], pos[1]);
		
		if (sizeString != null) {
			int[] size = Arrays.stream(sizeString.split(",")).mapToInt(Integer::parseInt).toArray();
			retVal.xSize = size[0];
			retVal.ySize = size[1];
		}
		
		return retVal;
	}
	
}
