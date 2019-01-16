package factory.subsystems.agv.interfaces;

import java.util.List;

import factory.shared.interfaces.Monitorable;
import factory.subsystems.agv.AgvTask;
import factory.subsystems.agv.Forklift;

public interface AgvMonitorInterface extends Monitorable{

	void submitTask(AgvTask task);	
	
	/**
	 * returns a list of all forklifts
	 * @return forklifts
	 */
	List<Forklift> getForklifts();
	
	List<AgvTask> getCurrentTasks();

}
