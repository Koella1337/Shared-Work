package factory.subsystems.warehouse.interfaces;

import java.util.List;

import factory.shared.enums.Material;
import factory.shared.interfaces.ContainerSupplier;
import factory.shared.interfaces.Monitorable;
import factory.subsystems.warehouse.WarehouseTask;

public interface WarehouseMonitorInterface extends Monitorable {

	int getContainerAmount(Material material);
	
	/**
	 * @param task
	 * @return the ResourceBox from which the demanded resource can be fetched
	 */
	public ContainerSupplier receiveTask(WarehouseTask task);
	
	//TODO change type
	public List<String> getTransactions();
}
