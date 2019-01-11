package factory.shared;

import app.gui.SubsystemMenu;
import factory.shared.interfaces.Monitorable;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public abstract class AbstractSubsystem implements Monitorable {

	private String name;
	private MonitoringInterface monitor;

	public AbstractSubsystem(MonitoringInterface monitor) {
		this(monitor,"-no name-");
	}
	
	public AbstractSubsystem(MonitoringInterface monitor, String name) {
		super();
		this.monitor = monitor;
		this.name = name;
	}
	
	@Override
	public void notify(FactoryEvent event) {
		monitor.handleEvent(event);
	}
	
	@Override
	public String getName() {
		return this.name;
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
