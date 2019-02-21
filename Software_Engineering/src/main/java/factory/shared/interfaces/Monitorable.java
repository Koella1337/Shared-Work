package factory.shared.interfaces;

import java.util.List;

import app.gui.SubsystemMenu;
import factory.shared.FactoryEvent;
import factory.shared.enums.SubsystemStatus;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public interface Monitorable extends Stoppable {

	MonitoringInterface getMonitor();
	
	String getName();
	
	void notify(FactoryEvent event);

	SubsystemStatus getStatus();

	List<Placeable> getPlaceables();
	
	SubsystemMenu getCurrentSubsystemMenu();

}
