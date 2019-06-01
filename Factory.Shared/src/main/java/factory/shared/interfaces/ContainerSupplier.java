package factory.shared.interfaces;

import factory.shared.Container;
import factory.shared.enums.Material;

/**
 * A physical (placeable) objects that can deliver containers.
 */
public interface ContainerSupplier extends Placeable {
	
	Container deliverContainer(Material material);
	
}
