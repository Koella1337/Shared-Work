package factory.shared;

import java.util.Objects;

import app.gui.SubsystemMenu;
import factory.shared.interfaces.Monitorable;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public abstract class AbstractSubsystem implements Monitorable {

	private final String name;
	private final MonitoringInterface monitor;

	public AbstractSubsystem(MonitoringInterface monitor) {
		this(monitor, null);
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
	public MonitoringInterface getMonitor() {
		return monitor;
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
	
}
