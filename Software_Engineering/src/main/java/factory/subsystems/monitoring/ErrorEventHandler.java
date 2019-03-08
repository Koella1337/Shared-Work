package factory.subsystems.monitoring;

import java.util.logging.Level;
import java.util.logging.Logger;

import factory.shared.FactoryEvent;
import factory.shared.enums.EventKind;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Monitorable;
import factory.subsystems.monitoring.interfaces.ErrorHandlerInterface;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

/**
 * This class will handle all events of type Error and Global Error
 * 
 * @author Sallaberger
 */
public class ErrorEventHandler implements ErrorHandlerInterface {
	private static final Logger LOGGER = Logger.getLogger(MonitoringSystem.class.getName());
	
	private final MonitoringInterface monitor;

	public ErrorEventHandler(MonitoringInterface monitor) {
		super();
		this.monitor = monitor;
	}

	/**
	 * Stop the whole system.
	 */
	@Override
	public void handleGlobalError(FactoryEvent event) {
		LOGGER.log(Level.WARNING, "[GLOBAL ERROR] " + event);
		this.monitor.setStatus(SubsystemStatus.BROKEN);
		this.monitor.stop();
	}

	/**
	 * Only stop affected subsystem.
	 */
	@Override
	public void handleError(Monitorable source, EventKind eventKind) {
		LOGGER.log(Level.WARNING, "[ERROR] " + eventKind);
		source.stop();
	}

	public MonitoringInterface getMonitor() {
		return monitor;
	}

	
	
}