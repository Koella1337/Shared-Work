 package factory.subsystems.warehouse;

import factory.shared.Task;
import factory.shared.enums.Material;

public class WarehouseTask extends Task {

	public Material material;
	
	public WarehouseTask(Material material) {
		super();
		this.material = material;
	}

	
}
