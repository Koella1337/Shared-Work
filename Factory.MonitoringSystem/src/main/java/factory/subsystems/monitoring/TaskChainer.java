package factory.subsystems.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import factory.subsystems.agv.AgvTask;
import factory.subsystems.agv.interfaces.AgvMonitorInterface;
import factory.subsystems.warehouse.WarehouseTask;
import factory.subsystems.warehouse.interfaces.WarehouseMonitorInterface;

/**
 * Allows chaining of Tasks by their id.<br>
 * Chain a Task after another Task by registering it as a followup-task.<br>
 * When a Task was completed, call notifyTaskCompleted(id) to automatically send the followup-tasks if there are any.
 */
public class TaskChainer {
	
	private final Map<Integer, List<WarehouseTask>> warehouseTaskChains = new HashMap<>();
	private final Map<Integer, List<AgvTask>> agvTaskChains = new HashMap<>();
	
	private final WarehouseMonitorInterface warehouseSubsystem;
	private final AgvMonitorInterface agvSubsystem;
	
	public TaskChainer(WarehouseMonitorInterface warehouseSubsystem, AgvMonitorInterface agvSubsystem) {
		this.warehouseSubsystem = warehouseSubsystem;
		this.agvSubsystem = agvSubsystem;
	}
	
	/**
	 * Checks for any followup-tasks and sends them to the respective subsystem if any are found.
	 * 
	 * @param completedTaskId - the id of the task that was just completed.
	 */
	public void notifyTaskCompleted(int completedTaskId) {
		this.notifyTaskCompleted(completedTaskId, null, null);
	}
	
	/**
	 * Checks for any followup-tasks and sends them to the respective subsystem if any are found.
	 * 
	 * @param completedTaskId - the id of the task that was just completed.
	 * @param warehouseTaskModifier - allows modifying of WarehouseTasks before they are sent. null is allowed.
	 * @param agvTaskModifier - allows modifying of AgvTasks before they are sent. null is allowed.
	 */
	public void notifyTaskCompleted(int completedTaskId, Consumer<WarehouseTask> warehouseTaskModifier, Consumer<AgvTask> agvTaskModifier) {
		List<WarehouseTask> warehouseFollowups = warehouseTaskChains.remove(completedTaskId);
		List<AgvTask> agvFollowups = agvTaskChains.remove(completedTaskId);
		
		if (warehouseFollowups != null) {
			for (WarehouseTask task : warehouseFollowups) {
				if (warehouseTaskModifier != null) {
					warehouseTaskModifier.accept(task);
				}
				warehouseSubsystem.receiveTask(task);
			}
		}
		
		if (agvFollowups != null) {
			for (AgvTask task : agvFollowups) {
				if (agvTaskModifier != null) {
					agvTaskModifier.accept(task);
				}
				agvSubsystem.receiveTask(task);
			}
		}
	}
	
	public void registerFollowupTask(int firstTaskId, WarehouseTask followupTask) {
		List<WarehouseTask> warehouseFollowups = warehouseTaskChains.get(firstTaskId);
		
		if (warehouseFollowups == null) {
			warehouseFollowups = new ArrayList<>();
			warehouseTaskChains.put(firstTaskId, warehouseFollowups);
		}
		
		warehouseFollowups.add(followupTask);
	}
	
	public void registerFollowupTask(int firstTaskId, AgvTask followupTask) {
		List<AgvTask> agvFollowups = agvTaskChains.get(firstTaskId);
		
		if (agvFollowups == null) {
			agvFollowups = new ArrayList<>();
			agvTaskChains.put(firstTaskId, agvFollowups);
		}
		
		agvFollowups.add(followupTask);
	}
	
}
