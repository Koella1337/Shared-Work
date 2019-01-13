package factory.shared.interfaces;

import factory.shared.Container;
import factory.shared.enums.Material;

public interface ContainerSupplier extends Placeable{
	
	Container deliverContainer(Material material);
	
}
