package factory.shared;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

@SuppressWarnings("serial")
public class TaskQueue extends PriorityBlockingQueue<Task> {
	
	public TaskQueue()
	{
		super();
	}
	
	public Comparator<Task> comparator()
	{
		return null;
	}
}
