package factory.shared;

public abstract class Task {
	private static int currentId = 1;
	
	private final int id;
	
	public Task()
	{
		this.id = currentId;
		currentId++;
	}
	
	public long getId() {
		return id;
	}
	
	

}
