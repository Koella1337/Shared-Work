package factory.subsystems.assemblyline.interfaces;

import factory.shared.enums.Material;
import factory.shared.interfaces.Monitorable;

public interface AssemblyLineSystemInterface extends Monitorable {

	/**
	 * Adds a custom, high-priority order.
	 */
	public void addCustomOrder(Material color, int quantity);

	/**
	 * Stops production of a specific car color.
	 */
	void stopProduction(Material color);
	
}
