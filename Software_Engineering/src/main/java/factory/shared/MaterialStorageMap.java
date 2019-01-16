package factory.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import factory.shared.enums.Material;

/**
 * Maps the type of a Material to the amount stored.
 */
public class MaterialStorageMap {

	private final Map<Material, Integer> map = new HashMap<>();
	
	public Map<Material, Integer> getMap() {
		return map;
	}
	
	public void add(Container container) {
		this.add(Objects.requireNonNull(container).getMaterial());
	}

	public void add(Material material) {
		Integer previousValue = map.get(material);
		if (previousValue != null)
			map.put(material, previousValue + 1);
		else
			map.put(material, 1);
	}
	
	public Integer get(Container container) {
		return this.get(container.getMaterial());
	}
	
	public Integer get(Material material) {
		return map.get(material);
	}
	
	public void remove(Container container) {
		this.remove(container.getMaterial());
	}
	
	/** @return the removed Material or null if nothing was removed. */
	public Material remove(Material material) {
		Integer previousValue = map.get(material);
		if (previousValue != null) {
			if (previousValue > 1)
				map.put(material, previousValue - 1);
			else
				map.remove(material);
			return material;
		}
		return null;
	}
	
}
