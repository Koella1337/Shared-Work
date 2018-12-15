package factory.shared;

import factory.shared.enums.Material;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;

public class Container {
	
	private final Material material;
	
	/**
	 * @param material - The material this container is filled with. Must not be null.
	 */
	public Container(Material material) {
		if (material == null)
			throw new IllegalArgumentException("Material in Container constructor must not be null.");
		this.material = material;
	}

	public Material getMaterial() {
		return material;
	}
	
	public int getAmount() {
		return material.containerAmount;
	}
	
	/**
	 * Transfers this container from a supplier to a demander.
	 */
	public void transfer(ContainerSupplier source, ContainerDemander target) {
		source.deliverContainer(this);
		target.receiveContainer(this);
	}
	
}