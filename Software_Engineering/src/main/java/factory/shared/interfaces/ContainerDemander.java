package factory.shared.interfaces;

import factory.shared.Container;

/**
 * A physical (placeable) objects that can receive containers.
 */
public interface ContainerDemander extends Placeable{

	void receiveContainer(Container container);
	
}