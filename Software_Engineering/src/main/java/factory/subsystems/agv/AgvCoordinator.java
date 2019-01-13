package factory.subsystems.agv;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import app.gui.SubsystemMenu;
import factory.shared.AbstractSubsystem;
import factory.shared.Task;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Placeable;
import factory.subsystems.agv.interfaces.AgvMonitorInterface;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public class AgvCoordinator extends AbstractSubsystem implements AgvMonitorInterface{
	
	private final List<Forklift> forklifts = new LinkedList<>();
	private SubsystemStatus status = SubsystemStatus.WAITING;
	private boolean ready = false;
	
	public AgvCoordinator(MonitoringInterface mon)
	{
		super(mon);
		status = SubsystemStatus.RUNNING;
		ready = true;
	}
	
	public void addForklift(Forklift forklift)
	{
		forklifts.add(forklift);
	}

	@Override
	public void submitTask(AgvTask task) {
		System.out.println("SUBMIT AGV TASK");
		
		
		
		// TODO: Choose best free Forklift and call forklift.assignTask
	}

	@Override
	public List<Forklift> getForklifts() {
		return forklifts;
	}

	@Override
	public SubsystemStatus getStatus() {
		return status;
	}

	@Override
	public List<Placeable> getPlaceables() {
		return new ArrayList<>(forklifts);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubsystemMenu getCurrentSubsystemMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Task getCurrentTask() {
		// TODO Auto-generated method stub
		return null;
	}
}
