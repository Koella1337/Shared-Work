package factory.subsystems.monitoring.onlineshop;

public class OnlineShopUser{

	private final String name;

	public OnlineShopUser(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "OnlineShopUser [name=" + name + "]";
	}
	
	
}
