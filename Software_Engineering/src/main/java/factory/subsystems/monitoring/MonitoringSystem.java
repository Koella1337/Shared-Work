package factory.subsystems.monitoring;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import app.gui.GUIHandler;
import factory.shared.AbstractSubsystem;
import factory.shared.FactoryEvent;
import factory.shared.enums.EventKind;
import factory.shared.enums.EventKind.EventSeverity;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Monitorable;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public class MonitoringSystem implements MonitoringInterface {
	private static final Logger LOGGER = Logger.getLogger(MonitoringSystem.class.getName());

	private SubsystemStatus status;

	private List<AbstractSubsystem> testList = new ArrayList<>(); // TODO replace by one list for each type of subsystem
	private GUIHandler handler;
	private ErrorEventHandler errorHandler;
	
	

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
	public void addToSubsystemList(AbstractSubsystem subsystem) {
		getTestSubSystemList().add(subsystem);
		
		//TODO: Alex hat des leida auskommentiern müssen :P
		//this.handler.addToFactoryPanel(subsystem);
	}

	@Override
	public void start() {
		for (AbstractSubsystem subsystem : getTestSubSystemList()) {
			LOGGER.log(Level.INFO, String.format("starting %s ...", subsystem.toString()));
			subsystem.start();
		}
		this.handler.start();
		this.setStatus(SubsystemStatus.RUNNING);
	}

	@Override
	public void stop() {

		for (AbstractSubsystem subsystem : getTestSubSystemList()) {
			try {
				LOGGER.log(Level.INFO, String.format("stopping %s ...", subsystem.toString()));
				subsystem.stop();
			} catch (Exception ex) {
				// this should not happen but if the system fails to stop one subsystem, the
				// others still should be stopped
				LOGGER.log(Level.SEVERE, ex.toString(), ex);
			}
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
	public List<AbstractSubsystem> getTestSubSystemList() {
		return testList;
	}

	@Override
	public SubsystemStatus getStatus() {
		return this.status;
	}

	
	@Override
	public void setStatus(SubsystemStatus status) {
		LOGGER.log(Level.INFO, String.format("Status set to %s",status));
		this.status = status;
	}

	@Override
	public void setCurrentSubsystemToShow(AbstractSubsystem subsystem) {
		this.handler.setCurrentSubsystem(subsystem);
	}


}
