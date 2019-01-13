package factory.subsystems.monitoring.interfaces;

import factory.shared.FactoryEvent;
import factory.shared.enums.EventKind;
import factory.shared.interfaces.Monitorable;

public interface ErrorHandlerInterface {

	 void handleGlobalError(FactoryEvent event);
	 void handleError(Monitorable source, EventKind eventKind);
}
