package factory.shared.interfaces;

import java.util.List;

import app.gui.SubsystemMenu;
import factory.shared.FactoryEvent;
import factory.shared.enums.SubsystemStatus;

public interface Monitorable extends Stoppable {

	String getName();
	
	void notify(FactoryEvent event);

	SubsystemStatus getStatus();

	List<Placeable> getPlaceables();
	
	SubsystemMenu getCurrentSubsystemMenu();

}
