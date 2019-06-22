package factory.subsystems.monitoring.interfaces;

import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.GUIHandler;
import factory.shared.interfaces.Monitor;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.agv.interfaces.AgvMonitorInterface;
import factory.subsystems.assemblyline.AssemblyLineSystem;
import factory.subsystems.warehouse.WarehouseSystem;
import factory.subsystems.warehouse.interfaces.WarehouseMonitorInterface;

public interface MonitoringInterface extends Monitor {

	/**
	 * handles the event by creating a task, stopping the system,...
	 * @param event
	 */
	void handleEvent(FactoryEvent event);

	void setGUIHandler(GUIHandler handler);
	
	/**
	 * @return the curent {@link SubsystemStatus} of the Subsystem
	 */
	SubsystemStatus getStatus();

	void setStatus(SubsystemStatus status);// TODO remove

	void setAgvSystem(AgvCoordinator agvSystem);

	AgvMonitorInterface getAgvSystem();

	void setWarehouseSystem(WarehouseSystem warehouseSystem);

	WarehouseMonitorInterface getWarehouseSystem();


	ResourceBox getShippingBox();

	void setShippingBox(Position shippingBoxPosition);

	AssemblyLineSystem getALSubsys();

	void setAssemblyLine(AssemblyLineSystem alSystem);


	Position getStaffQuarterPosition();

	void setStaffQuarterPosition(Position staffQuarterPosition);
}
