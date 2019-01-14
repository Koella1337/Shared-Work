package factory.subsystems.warehouse;

import factory.shared.Position;

/**
 * Simulated forklift that is used within StorageSites.
 */
public class WarehouseForklift {
	
	private Position pos;
	
	public WarehouseForklift(Position startPos) {
		this.pos = startPos;
	}
	
}
