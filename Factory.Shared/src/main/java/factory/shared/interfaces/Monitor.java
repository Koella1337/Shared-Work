package factory.shared.interfaces;

import factory.shared.FactoryEvent;

public interface Monitor extends Stoppable {

	public void handleEvent(FactoryEvent event);
	
}
