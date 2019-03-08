package factory.subsystems.agv;

import factory.shared.Task;
import factory.shared.enums.Material;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;

public class AgvTask extends Task {

	private final Material material;
	
	private ContainerSupplier pickup;
	private ContainerDemander dropoff;

	public AgvTask(long timeframe, Material material,  ContainerSupplier pickup, ContainerDemander dropoff) {
		super(timeframe);
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

	public void setPickup(ContainerSupplier pickup) {
		this.pickup = pickup;
	}

	public void setDropoff(ContainerDemander dropoff) {
		this.dropoff = dropoff;
	}
	
}
