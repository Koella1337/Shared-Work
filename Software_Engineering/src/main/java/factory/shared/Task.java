package factory.shared;

public abstract class Task implements Comparable<Task> {
	
	private static int currentId = 1;	//TODO remove ?
	
	private final int id;
	private final long deadline;
	
	public Task(long timeframe) {
		this.id = currentId;
		currentId++;
		deadline = timeframe + System.currentTimeMillis();
	}
	
	public long getId() {
		return id;
	}
	
	public int compareTo(Task other) {
		// as long as the time difference is less than ~600h converting to int should be fine
		return (int) (deadline - other.deadline);
	}
	
	public long getTimeLeft() {
		return deadline - System.currentTimeMillis();
	}
}
