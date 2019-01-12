package factory.subsystems.monitoring.onlineshop;

import java.util.Objects;

public class Order {

	private final OnlineShopUser user;
	private final int amount;
	
	public Order(OnlineShopUser user, int amount) {
		super();
		this.user = Objects.requireNonNull(user);
		this.amount = amount;
	}
	
	public final OnlineShopUser getUser() {
		return user;
	}
	public final int getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return "Order [user=" + user + ", amount=" + amount + "]";
	}
	
}
