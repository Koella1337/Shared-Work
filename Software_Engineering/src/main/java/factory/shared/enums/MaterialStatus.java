package factory.shared.enums;

import java.awt.Color;

/** 
 * Portrays how the status (in regards to Materials) of a certain Placeable is. <br>
 * Examples: <br>
 *   - How well a shelf is sorted<br>
 *   - How full a ResourceBox is<br>
 *   - ...
 */
public enum MaterialStatus {
	PERFECT			(new Color(  0, 170,  0)),	//dark green
	WELL			(new Color(145, 245, 65)),	//lime-green
	AVERAGE			(Color.YELLOW),
	BAD				(new Color(255, 123, 0)),	//orange
	TERRIBLE		(Color.RED),
	
	EMPTY			(Color.LIGHT_GRAY);
	
	/** The color that the Placeable could be displayed with on the UI regarding its MaterialStatus. */
	public final Color uiColor;
	
	private MaterialStatus(Color uiColor) {
		this.uiColor = uiColor;
	}
	
	public static MaterialStatus getBestStatus() {
		return PERFECT;
	}
	
	public static MaterialStatus getNextWorseStatus(MaterialStatus currentStatus) {
		if (currentStatus == null)
			return TERRIBLE;
		
		switch(currentStatus) {
			case PERFECT: 	return WELL;
			case WELL: 		return AVERAGE;
			case AVERAGE: 	return BAD;
			case BAD: 		return TERRIBLE;
			case TERRIBLE:	return TERRIBLE;
			default: 		return TERRIBLE;
		}
	}
}
