 package factory.subsystems.warehouse;

import factory.shared.Task;
import factory.shared.enums.Material;

public class WarehouseTask extends Task {

	private Material material;
	
	public WarehouseTask(Material material) {
		super();
		this.material = material;
	}

	public Material getMaterial() {
		return material;
	}

	
}
