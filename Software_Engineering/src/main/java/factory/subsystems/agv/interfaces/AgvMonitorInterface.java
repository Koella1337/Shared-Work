package factory.subsystems.agv.interfaces;

import factory.shared.Task;
import factory.shared.interfaces.Monitorable;
import factory.subsystems.agv.Forklift;

import java.util.List;

public interface AgvMonitorInterface extends Monitorable{

	void submitTask(Task task);	
	
	/**
	 * returns a list of all forklifts
	 * @return forklifts
	 */
	List<Forklift> getForklifts();
	
	Task getCurrentTask();

}
