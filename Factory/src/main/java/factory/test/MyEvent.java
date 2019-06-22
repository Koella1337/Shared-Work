package factory.test;

public class MyEvent {

	private String name;

	public MyEvent(String name) {
		super();
		this.name = name;
		System.out.println("-- created Event "+name);
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
