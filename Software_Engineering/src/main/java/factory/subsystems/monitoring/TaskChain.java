package factory.subsystems.monitoring;

import java.util.LinkedList;

import factory.shared.Task;

public class TaskChain {

	private final MonitoringSystem monitoringSystem;
	private final LinkedList<Task> tasklist;

	

	public TaskChain(MonitoringSystem monitoringSystem) {
		super();
		this.monitoringSystem = monitoringSystem;
		this.tasklist = new LinkedList<>();
	}

	public void addTask(Task task) {
		tasklist.add(task);
	}
	
	public Task getNextTask() {
		return tasklist.poll();
	}

	public MonitoringSystem getMonitoringSystem() {
		return monitoringSystem;
	}

	public LinkedList<Task> getTasklist() {
		return tasklist;
	}

	
	
	
}
