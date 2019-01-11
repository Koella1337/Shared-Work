package factory.subsystems.monitoring.interfaces;

import java.util.List;

import factory.shared.AbstractSubsystem;
import factory.shared.FactoryEvent;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Stoppable;

public interface MonitoringInterface extends Stoppable {

	// TODO create methods like addRobotSystem, etc.

	public void handleEvent(FactoryEvent event);

	public void addToSubsystemList(AbstractSubsystem subsystem);

	public SubsystemStatus getStatus();
	
	
	public void setStatus(SubsystemStatus status);//TODO remove

	public void setCurrentSubsystemToShow(AbstractSubsystem subsystem);
	
	public List<AbstractSubsystem> getTestSubSystemList();

}
