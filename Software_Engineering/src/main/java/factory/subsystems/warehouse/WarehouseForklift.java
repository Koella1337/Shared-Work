package factory.subsystems.warehouse;

import java.awt.Color;
import java.awt.Graphics;

import factory.shared.Constants;
import factory.shared.Constants.PlaceableSize;
import factory.shared.Container;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.enums.Material;
import factory.shared.interfaces.Placeable;

/**
 * Simulated forklift that is used within StorageSites.
 */
class WarehouseForklift implements Placeable {
	
	private static final int SIM_MOVEMENT_MULTIPLIER = 20;
	
	private static final int SIM_DURATION_SORTSHELF = 4500;
	private static final int SIM_DURATION_GRABCONTAINER = 1000;
	private static final int SIM_DURATION_DELIVERCONTAINER = 1000;
	
	private static final int X_SIZE = PlaceableSize.SHELF.x;
	private static final int Y_SIZE = X_SIZE;
	
	private static final Color WORKING_COLOR = Color.MAGENTA;
	private static final Color TRAVELLING_COLOR = Color.PINK;
	
	private final StorageSite storageSite;
	
	private Position pos;
	private Color color;
	
	public WarehouseForklift(StorageSite storageSite, Position startPos) {
		this.storageSite = storageSite;
		this.pos = startPos;
		this.color = WORKING_COLOR;
	}

	protected void moveTo(Position goal) throws InterruptedException {
		this.color = TRAVELLING_COLOR;
		int moveDuration = (int) (Position.length(Position.subtractPosition(goal, this.pos)) * SIM_MOVEMENT_MULTIPLIER);
		Thread.sleep(moveDuration);
		this.pos = goal;
		this.color = WORKING_COLOR;
	}
	
	protected void moveTo(Shelf shelf) throws InterruptedException {
		moveTo(Position.add(shelf.getPosition(), 0, PlaceableSize.SHELF.y / 2 - Y_SIZE / 2));
	}
	
	protected void moveTo(ResourceBox box) throws InterruptedException {
		PlaceableSize boxSize = PlaceableSize.RESOURCE_BOX;
		moveTo(Position.add(box.getPosition(), boxSize.x / 2 - X_SIZE / 2, 2*(boxSize.y / 3 + Y_SIZE / 3)));
	}
	
	protected void sortShelf(Shelf shelf) throws InterruptedException {
		moveTo(shelf);
		Thread.sleep(SIM_DURATION_SORTSHELF);
		shelf.sortShelf();
	}
	
	protected void doTask(WarehouseTask task) throws InterruptedException {
		Material material = task.getMaterial();		
		Shelf shelf = storageSite.getShelfForMaterial(material);
		ResourceBox box = storageSite.getOutputbox();
		
		moveTo(shelf);
		Thread.sleep(SIM_DURATION_GRABCONTAINER);
		Container container = shelf.deliverContainer(material);
		moveTo(box);
		Thread.sleep(SIM_DURATION_DELIVERCONTAINER);
		box.receiveContainer(container);
	}
	
	protected void fetchFromInputbox(Material materialToFetch) throws InterruptedException {
		Shelf shelf = storageSite.getFreeShelf();
		ResourceBox box = storageSite.getInputbox();
		
		moveTo(box);
		Thread.sleep(SIM_DURATION_GRABCONTAINER);
		Container container = box.deliverContainer(materialToFetch);
		moveTo(shelf);
		Thread.sleep(SIM_DURATION_DELIVERCONTAINER);
		shelf.receiveContainer(container);
	}

	@Override
	public Position getPosition() {
		return pos;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(1, 1, X_SIZE - 1, Y_SIZE - 1);
		g.setColor(Constants.UI_BORDER_COLOR);
		g.drawRect(0, 0, X_SIZE, Y_SIZE);
	}
	
}
