package factory.shared;

import java.util.Objects;

import factory.shared.interfaces.Monitor;
import factory.shared.interfaces.Monitorable;

public abstract class AbstractSubsystem implements Monitorable {

	private final String name;
	private final Monitor monitor;

	public AbstractSubsystem(Monitor monitor) {
		this(monitor, null);
	}
	
	public AbstractSubsystem(Monitor monitor, String name) {
		this.monitor = Objects.requireNonNull(monitor);
		this.name = name;
	}
	
	@Override
	public void notify(FactoryEvent event) {
		monitor.handleEvent(event);
	}
	
	@Override
	public Monitor getMonitor() {
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
	
}
