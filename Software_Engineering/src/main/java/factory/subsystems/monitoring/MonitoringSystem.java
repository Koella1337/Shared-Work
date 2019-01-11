package factory.subsystems.monitoring;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import app.gui.GUIHandler;
import factory.shared.AbstractSubsystem;
import factory.shared.FactoryEvent;
import factory.shared.enums.EventKind;
import factory.shared.enums.EventKind.EventSeverity;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Monitorable;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.warehouse.WarehouseSystem;

public class MonitoringSystem implements MonitoringInterface {
	private static final Logger LOGGER = Logger.getLogger(MonitoringSystem.class.getName());

	private final GUIHandler handler;
	private final ErrorEventHandler errorHandler;

	private SubsystemStatus status;
	private AgvCoordinator agvSystem;
	private WarehouseSystem warehouseSystem;

	public MonitoringSystem() {
		this.handler = new GUIHandler(this);
		this.errorHandler = new ErrorEventHandler(this);
	}

	@Override
	public synchronized void handleEvent(FactoryEvent event) {
		try {
			LOGGER.log(Level.INFO, String.format("handling event %s ...", event));
			Monitorable source = event.getSource();
			EventKind eventKind = event.getKind();
			EventSeverity severity = eventKind.severity;
			switch (severity) {
			case GLOBAL_EROR:
				this.getErrorHandler().handleGlobalError(event);
				this.setStatus(SubsystemStatus.BROKEN);
				break;
			case ERROR:
				getErrorHandler().handleError(source, eventKind);
				break;
			case IMPORTANT:
				break;
			case INFO:
				break;
			case NORMAL:
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			handleEventHandlingException(event, ex);
		}
	}

	@Override
	public void start() {
		Objects.requireNonNull(this.agvSystem);// TODO @thomas throw exception
		Objects.requireNonNull(this.warehouseSystem);

		this.agvSystem.start();
		this.warehouseSystem.start();

		this.handler.start();
		this.setStatus(SubsystemStatus.RUNNING);
	}

	@Override
	public void stop() {
		try {
			this.agvSystem.stop();
			this.warehouseSystem.stop();
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.toString(), ex);
		}
		this.setStatus(SubsystemStatus.STOPPED);
	}

	/**
	 * if the
	 * 
	 * @param ex
	 */
	private void handleEventHandlingException(FactoryEvent event, Exception ex) {
		LOGGER.log(Level.SEVERE, ex.toString(), ex);
		getErrorHandler().handleGlobalError(event);
		this.setStatus(SubsystemStatus.BROKEN);
	}

	protected ErrorEventHandler getErrorHandler() {
		return errorHandler;
	}

	@Override
	public SubsystemStatus getStatus() {
		return this.status;
	}

	@Override
	public void setStatus(SubsystemStatus status) {
		LOGGER.log(Level.INFO, String.format("Status set to %s", status));
		this.status = status;
	}

	@Override
	public void setCurrentSubsystemToShow(AbstractSubsystem subsystem) {
		this.handler.setCurrentSubsystem(subsystem);
	}

	@Override
	public AgvCoordinator getAgvSystem() {
		return agvSystem;
	}

	@Override
	public void setAgvSystem(AgvCoordinator agvSystem) {
		this.handler.addToFactoryPanel(agvSystem);
		this.agvSystem = agvSystem;
	}

	@Override
	public WarehouseSystem getWarehouseSystem() {
		return warehouseSystem;
	}

	@Override
	public void setWarehouseSystem(WarehouseSystem warehouseSystem) {
		this.handler.addToFactoryPanel(warehouseSystem);
		this.warehouseSystem = warehouseSystem;
	}

}
