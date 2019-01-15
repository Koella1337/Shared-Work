package factory.shared.enums;

import java.awt.Color;
import java.util.Comparator;

/** 
 * Portrays how the status (in regards to Materials) of a certain Placeable is. <br>
 * Examples: <br>
 *   - How well a shelf is sorted<br>
 *   - How full a ResourceBox is<br>
 *   - ...
 */
public enum MaterialStatus {
	EMPTY			(Color.LIGHT_GRAY),
	
	PERFECT			(new Color(  0, 170,  0)),	//dark green
	WELL			(new Color(145, 245, 65)),	//lime-green
	AVERAGE			(Color.YELLOW),
	BAD				(new Color(255, 125,  0)),	//orange
	TERRIBLE		(Color.RED);
	
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
	
	/** Returns a comparator for sorting by MaterialStatus. Worst = First */
	public static Comparator<MaterialStatus> comparator() {
		return (status1, status2) -> {
			if (status1 == status2)
				return 0;
			else
				return status1.isWorse(status2) ? -1 : 1;
		};
	}
	
	/** Returns whether this status is worse than the specified status. */
	public boolean isWorse(MaterialStatus otherStatus) {
		return this.ordinal() > otherStatus.ordinal();
	}

}
