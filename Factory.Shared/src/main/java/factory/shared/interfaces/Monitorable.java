package factory.shared.interfaces;

import java.util.List;

import factory.shared.FactoryEvent;
import factory.shared.enums.SubsystemStatus;

public interface Monitorable extends Stoppable {

	Monitor getMonitor();
	
	String getName();
	
	void notify(FactoryEvent event);

	SubsystemStatus getStatus();

	List<Placeable> getPlaceables();
	
}
