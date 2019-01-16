package factory.shared;

import java.util.Objects;

import app.gui.SubsystemMenu;
import factory.shared.interfaces.Monitorable;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public abstract class AbstractSubsystem implements Monitorable {

	private String name;
	private MonitoringInterface monitor;

	public AbstractSubsystem(MonitoringInterface monitor) {
		this(Objects.requireNonNull(monitor), null);
	}
	
	public AbstractSubsystem(MonitoringInterface monitor, String name) {
		this.monitor = Objects.requireNonNull(monitor);
		this.name = name;
	}
	
	@Override
	public void notify(FactoryEvent event) {
		monitor.handleEvent(event);
	}
	
	@Override
	public String getName() {
		if (name != null)
			return name;
		else
			return this.getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public SubsystemMenu getCurrentSubsystemMenu() {
		return new SubsystemMenu(30, "Subsystem: "+getName());
	}

	public MonitoringInterface getMonitor() {
		return monitor;
	}
	
	
}
