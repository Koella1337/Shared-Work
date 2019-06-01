package factory.shared.interfaces;

import factory.shared.AbstractSubsystem;

public interface GUIHandler extends Stoppable {

	public void addToFactoryPanel(AbstractSubsystem subsystem);
	
}
