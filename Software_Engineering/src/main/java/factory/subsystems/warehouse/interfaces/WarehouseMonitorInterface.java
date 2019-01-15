package factory.subsystems.warehouse.interfaces;

import java.util.List;

import factory.shared.enums.Material;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.subsystems.warehouse.Transaction;
import factory.subsystems.warehouse.WarehouseTask;

public interface WarehouseMonitorInterface extends Monitorable {

	int getContainerAmount(Material material);
	
	/**
	 * @param task
	 * @return the ResourceBox from which the demanded resource can be fetched
	 */
	public void receiveTask(WarehouseTask task);
	
	public Transaction[] getTransactions();
	
	public List<Placeable> getOutputBoxes();
}
