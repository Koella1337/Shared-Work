package factory.subsystems.warehouse;

import java.awt.Graphics;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import factory.shared.Constants;
import factory.shared.Container;
import factory.shared.MaterialStorageMap;
import factory.shared.Position;
import factory.shared.enums.Material;
import factory.shared.enums.MaterialStatus;
import factory.shared.interfaces.Placeable;

public class Shelf implements Placeable {
	
	private final Position pos;
	private final ItemSlot[] slots;
	
	public Shelf(Position position) {
		this.pos = Objects.requireNonNull(position);
		this.slots = new ItemSlot[Constants.SHELF_SLOT_AMOUNT];
		
		if (slots.length < 1)
			throw new IllegalArgumentException("Shelf must have at least 1 Slot!");
		
		for (int i = 0; i < slots.length; i++) {
			slots[i] = new ItemSlot();
		}
		
		//TODO: remove !!
		Random rng = new Random();
		int rnd = rng.nextInt(20);
		for (int i = 0; i < rnd; i++) {
			this.storeContainer(new Container(Material.CAR_BODIES));
		}
		rnd = rng.nextInt(10);
		for (int i = 0; i < rnd; i++) {
			this.takeContainer(Material.CAR_BODIES);
		}
		//System.out.println(this.toString());
		this.sortShelf();
	}
	
	/** @return a Container of the supplied Material, or null if that material isn't stored in this shelf */
	protected Container takeContainer(Material material) {
		for (int i = 0; i < slots.length; i++) {
			Container container = slots[i].takeContainer(material);
			if (container != null) return container;
		}
		return null;
	}
	
	/** @return the row the container will be stored in or -1 if the Shelf is full. */
	protected int storeContainer(Container container) {
		for (int i = 0; i < slots.length; i++) {
			int column = slots[i].storeContainer(container);
			if (column != -1) return i;
		}
		return -1;
	}
	
	protected boolean hasMaterial(Material material) {
		for (int i = 0; i < slots.length; i++) {
			if (slots[i].hasMaterial(material)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean hasFreeSlot() {
		for (int i = 0; i < slots.length; i++) {
			if (slots[i].canTakeContainer()) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isEmpty() {
		for (int i = 0; i < slots.length; i++) {
			if (!slots[i].isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	protected MaterialStatus getShelfSortedness() {
		if (this.isEmpty())
			return MaterialStatus.EMPTY;

		MaterialStatus curStatus = MaterialStatus.getBestStatus(); 
		boolean inEmptySlotArea = false;			//emptySlotArea is activated when all slots from that row on should be empty
		boolean wasPreviousSlotEmpty = false;
		
		for (int i = 0; i < slots.length; i++) {
			boolean curSlotEmpty = slots[i].isEmpty();
			
			if (!inEmptySlotArea && !wasPreviousSlotEmpty && curSlotEmpty) {
				if (i == 0)	//first slot should not be free
					curStatus = MaterialStatus.getNextWorseStatus(curStatus);	
				else
					inEmptySlotArea = true;
			}
			else if (inEmptySlotArea && !curSlotEmpty) {
				curStatus = MaterialStatus.getNextWorseStatus(curStatus);
			}
			
			wasPreviousSlotEmpty = curSlotEmpty;
		}
		return curStatus;
	}
	
	protected void sortShelf() {
		MaterialStorageMap allMaterials = new MaterialStorageMap();
		
		Arrays.stream(slots).forEach(slot -> {
			Arrays.stream(slot.getContent()).forEach(mat -> allMaterials.add(mat));
			slot.clear();
		});
		
		for (Material mat : allMaterials.getMap().keySet()) {
			if (mat == null) continue;
			
			while (allMaterials.get(mat) != null) {
				allMaterials.remove(mat);
				this.storeContainer(new Container(mat));
			}
		}
	}

	@Override
	public Position getPosition() {
		return pos;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Constants.UI_BORDER_COLOR);
		g.drawRect(0, 0, pos.xSize, pos.ySize);
		g.setColor(this.getShelfSortedness().uiColor);
		g.fillRect(1, 1, pos.xSize - 1, pos.ySize - 1);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Shelf Pos: ");
		sb.append(pos.toString());
		sb.append('\n');
		for (ItemSlot s : slots) {
			sb.append(s.toString());
			sb.append('\n');
		}
		return sb.toString();
	}
	
	private class ItemSlot {
		
		private final Material[] content;

		public ItemSlot() {
			content = new Material[Constants.SHELF_SLOT_CAPACITY];
			clear();
		}
		
		/** Clears the content of this slot (resets content array). */
		public void clear() {
			for (int i = 0; i < content.length; i++)
				content[i] = null;
		}
		
		public Material[] getContent() {
			return content;
		}

		/** @return a Container of the supplied Material, or null if that material isn't stored in this slot */
		public Container takeContainer(Material material) {
			for (int i = 0; i < content.length; i++) {
				if (content[i] == material) {
					content[i] = null;
					return new Container(material);
				}
			}
			return null;
		}
		
		/** @return the column the container will be stored in or -1 if the Slot is full. */
		public int storeContainer(Container container) {
			for (int i = 0; i < content.length; i++) {
				if (content[i] == null) {
					content[i] = container.getMaterial();
					return i;
				}
			}
			return -1;
		}
		
		public boolean isEmpty() {
			for (int i = 0; i < content.length; i++) {
				if (content[i] != null) {
					return false;
				}
			}
			return true;
		}
		
		public boolean canTakeContainer() {
			for (int i = 0; i < content.length; i++) {
				if (content[i] == null) {
					return true;
				}
			}
			return false;
		}
		
		public boolean hasMaterial(Material material) {
			for (int i = 0; i < content.length; i++) {
				if (content[i] == material) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("    (ItemSlot content: ");
			for (int i = 0; i < content.length; i++) {
				if (i != 0) sb.append(", ");
				sb.append(content[i]);
			}
			sb.append(")");
			return sb.toString();
		}
	}

}
