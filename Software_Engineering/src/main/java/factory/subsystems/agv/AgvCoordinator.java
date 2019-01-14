package factory.subsystems.agv;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import app.gui.SubsystemMenu;
import factory.shared.AbstractSubsystem;
import factory.shared.Position;
import factory.shared.Task;
import factory.shared.Utils;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Placeable;
import factory.subsystems.agv.interfaces.AgvMonitorInterface;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public class AgvCoordinator extends AbstractSubsystem implements AgvMonitorInterface{
	
	private final List<Forklift> forklifts = new LinkedList<>();
	private SubsystemStatus status = SubsystemStatus.WAITING;
	private boolean ready = false;
	private List<AgvTask> tasks = new LinkedList<>();
	
	public AgvCoordinator(MonitoringInterface mon, Element factory)
	{
		super(mon);
		status = SubsystemStatus.RUNNING;
		ready = true;
		
		// TODO: Read map and construct factory
		Element forks = (Element) factory.getElementsByTagName("forklifts").item(0);
		NodeList forkliftElements = forks.getElementsByTagName("forklift");
		for(int i = 0; i < forkliftElements.getLength(); i++)
		{
			Node a = ((Element)forkliftElements.item(i)).getElementsByTagName("position").item(0);
			String s = a.getFirstChild().getNodeValue();
			Position p = Utils.parsePosition(s, null);
			Forklift f = new Forklift(p, this);
			addForklift(f);
		}
	}
	
	public void addForklift(Forklift forklift)
	{
		forklifts.add(forklift);
	}

	@Override
	public void submitTask(AgvTask task) {
		System.out.println("SUBMIT AGV TASK");
		
		// TODO: Choose closest free Forklift and call forklift.assignTask
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
		return "AGV System";
	}

	@Override
	public SubsystemMenu getCurrentSubsystemMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start() {
		for(Forklift f : forklifts)
		{
			f.shutdown();
		}
	}

	@Override
	public void stop() {
		for(Forklift f : forklifts)
		{
			f.resume();
		}
	}

	@Override
	public List<AgvTask> getCurrentTasks() {
		return tasks;
	}
}
