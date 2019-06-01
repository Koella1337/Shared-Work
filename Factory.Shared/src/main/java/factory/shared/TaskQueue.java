package factory.shared;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

@SuppressWarnings("serial")
public class TaskQueue<T extends Task> extends PriorityBlockingQueue<T> {
	
	public TaskQueue() {
		super();
	}
	
	public Comparator<Task> comparator() {
		return null;	//natural order of Comparable Task
	}
}
