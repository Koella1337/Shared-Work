package factory.shared;

import java.util.Objects;

import factory.shared.enums.Material;

public class Container {
	
	private final Material material;
	
	/**
	 * @param material - The material this container is filled with. Must not be null.
	 */
	public Container(Material material) {
		this.material = Objects.requireNonNull(material, "Material in Container constructor must not be null.");
	}

	public Material getMaterial() {
		return material;
	}
	
	public int getAmount() {
		return material.containerAmount;
	}
	
	@Override
	public String toString() {
		return "(Container: " + material + ")";
	}
	
}
