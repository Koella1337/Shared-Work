package factory.shared;

public class Constants {
	
	public enum PlaceableSize {
		SHELF				(  8, 35),
		RESOURCE_BOX		( 40, 40),
		
		FORKLIFT			( 10, 10),
		
		ASSEMBLY_LINE		(350,100);
		
		public final int x, y;
		
		private PlaceableSize(int xSize, int ySize) {
			this.x = xSize;
			this.y = ySize;
		}
	}
	
	/** How much offset does a StorageSite shelf have from walls and boxes (and horizontally from other shelves). */
	public static final int SHELF_OFFSET = 12;
	
	public static final int SHELF_SLOT_AMOUNT = 4;
	public static final int SHELF_SLOT_CAPACITY = 5;
	
	public static final int UI_WIDTH_MENU = 400;
	
}
