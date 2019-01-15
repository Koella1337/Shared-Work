package factory.shared;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Constants {
	
	public enum PlaceableSize {
		SHELF				(  8, 35),
		RESOURCE_BOX		( 40, 40),
		
		FORKLIFT			( 20, 20),
		
		ASSEMBLY_LINE		(350,100);
		
		public final int x, y;
		
		private PlaceableSize(int xSize, int ySize) {
			this.x = xSize;
			this.y = ySize;
		}
	}
	
	/** toggle console output */
	public static final boolean DEBUG = true;
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy");
	
	public static final int RESOURCE_BOX_MAX_CONTAINERS = 100;
	
	/** The amount of containers the warehouse should at least have for each Material. */
	public static final int WAREHOUSE_RESUPPLY_THRESHOLD = 30;
	/** The minimum amount that can be ordered for resupplying a Material. */
	public static final int WAREHOUSE_RESUPPLY_MIN_AMOUNT = 10;
	
	/** How much offset does a StorageSite shelf have from walls and boxes (and horizontally from other shelves). */
	public static final int SHELF_OFFSET = 12;
	
	public static final int SHELF_SLOT_AMOUNT = 3;
	public static final int SHELF_SLOT_CAPACITY = 5;
	
	public static final int UI_WIDTH_MENU = 400;
	public static final Color UI_BORDER_COLOR = Color.DARK_GRAY;
	
	public static final String testUserName = "test";
	public static final String testPassword = "1234";
	
}
