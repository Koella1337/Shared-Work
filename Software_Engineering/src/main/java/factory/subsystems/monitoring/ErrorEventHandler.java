package factory.subsystems.monitoring;

import java.util.logging.Level;
import java.util.logging.Logger;

import factory.shared.FactoryEvent;
import factory.shared.enums.EventKind;
import factory.shared.interfaces.Monitorable;
import factory.subsystems.monitoring.interfaces.ErrorHandlerInterface;

/**
 * This class will handle all events of type Error and Global Error
 * 
 * @author Sallaberger
 */
public class ErrorEventHandler implements ErrorHandlerInterface {
	private static final Logger LOGGER = Logger.getLogger(ErrorEventHandler.class.getName());
	
	private final MonitoringSystem monitor;

	public ErrorEventHandler(MonitoringSystem monitor) {
		super();
		this.monitor = monitor;
	}

	@Override
	public void handleGlobalError(FactoryEvent event) {
		LOGGER.log(Level.WARNING, String.format("handling global error %s ...",event));
		this.monitor.stop();
	}

	@Override
	public void handleError(Monitorable source, EventKind eventKind) {
		source.stop();
	}

	public MonitoringSystem getMonitor() {
		return monitor;
	}

	
	
}