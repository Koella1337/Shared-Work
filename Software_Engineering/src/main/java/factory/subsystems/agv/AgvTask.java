package factory.subsystems.agv;

import factory.shared.Task;
import factory.shared.enums.Material;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;

public class AgvTask extends Task {

	private final ContainerSupplier pickup;
	private final ContainerDemander dropoff;
	private final Material material;

	public AgvTask( Material material,  ContainerSupplier pickup, ContainerDemander dropoff) {
		super();
		this.pickup = pickup;
		this.dropoff = dropoff;
		this.material = material;
	}
	
	public ContainerSupplier getPickup() {
		return pickup;
	}

	public ContainerDemander getDropoff() {
		return dropoff;
	}

	public Material getMaterial() {
		return material;
	}

	

}
