package factory.subsystems.monitoring.interfaces;

import java.util.List;

import factory.shared.AbstractSubsystem;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.agv.AgvTask;
import factory.subsystems.agv.interfaces.AgvMonitorInterface;
import factory.subsystems.monitoring.onlineshop.Order;
import factory.subsystems.warehouse.WarehouseSystem;
import factory.subsystems.warehouse.WarehouseTask;
import factory.subsystems.warehouse.interfaces.WarehouseMonitorInterface;

public interface MonitoringInterface extends Stoppable {

	/**
	 * handles the event by creating a task, stopping the system,...
	 * @param event
	 */
	void handleEvent(FactoryEvent event);

	
	/**
	 * @return the curent {@link SubsystemStatus} of the Subsystem
	 */
	SubsystemStatus getStatus();

	void setStatus(SubsystemStatus status);// TODO remove

	void setCurrentSubsystemToShow(AbstractSubsystem subsystem);

	void setAgvSystem(AgvCoordinator agvSystem);

	AgvMonitorInterface getAgvSystem();

	void setWarehouseSystem(WarehouseSystem warehouseSystem);

	WarehouseMonitorInterface getWarehouseSystem();

	void addOrder(Order order);


	List<WarehouseTask> getWarehouseTaskList();


	List<AgvTask> getAgvTaskList();


	ResourceBox getShippingBox();

	void setShippingBoxPosition(Position shippingBoxPosition);


	Position getShippingBoxPosition();


	void setShippingBox(ResourceBox shippingBox);
}
