package factory.subsystems.warehouse;

import java.awt.Color;
import java.awt.Graphics;

import factory.shared.Constants;
import factory.shared.Container;
import factory.shared.Position;
import factory.shared.enums.Material;
import factory.shared.interfaces.Placeable;

public class Shelf implements Placeable {

	private final Position pos;
	private final ItemSlot[] slots;
	
	public Shelf(Position position) {
		this.pos = position;
		slots = new ItemSlot[Constants.SHELF_SLOT_AMOUNT];
	}

	@Override
	public Position getPosition() {
		return pos;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.DARK_GRAY);	//TODO
		g.drawRect(0, 0, pos.xSize, pos.ySize);
	}
	
	
	private class ItemSlot {
		
		private final int row;
		private final Material[] content;

		public ItemSlot(int row) {
			this.row = row;
			content = new Material[Constants.SHELF_SLOT_CAPACITY];
		}
		
		/**
		 * Returns the column the container will be stored in or -1 if the Slot is full.
		 */
		public int storeContainer(Container container) {
			for (int i = 0; i < content.length; i++) {
				if (content[i] == null) {
					content[i] = container.getMaterial();
					return i;
				}
			}
			return -1;
		}
		
	}

}
