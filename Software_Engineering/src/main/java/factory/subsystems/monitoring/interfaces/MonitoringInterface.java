package factory.subsystems.monitoring.interfaces;

import factory.shared.AbstractSubsystem;
import factory.shared.FactoryEvent;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.agv.interfaces.AgvMonitorInterface;
import factory.subsystems.warehouse.WarehouseSystem;
import factory.subsystems.warehouse.interfaces.WarehouseMonitorInterface;

public interface MonitoringInterface extends Stoppable {

	/**
	 * handles the event by creating a task, stopping the system,...
	 * @param event
	 */
	public void handleEvent(FactoryEvent event);

	
	/**
	 * @return the curent {@link SubsystemStatus} of the Subsystem
	 */
	public SubsystemStatus getStatus();

	public void setStatus(SubsystemStatus status);// TODO remove

	public void setCurrentSubsystemToShow(AbstractSubsystem subsystem);

	public void setAgvSystem(AgvCoordinator agvSystem);

	public AgvMonitorInterface getAgvSystem();

	public void setWarehouseSystem(WarehouseSystem warehouseSystem);

	public WarehouseMonitorInterface getWarehouseSystem();
}
