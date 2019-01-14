package factory.subsystems.monitoring.onlineshop;

import factory.shared.enums.Material;

public class Order {

	private final OnlineShopUser user;
	private final int amount;
	private final Material color;

	public Order(OnlineShopUser user, int amount, Material color) {
		super();
		this.user = user;
		this.amount = amount;
		this.color = color;
	}

	public OnlineShopUser getUser() {
		return user;
	}

	public int getAmount() {
		return amount;
	}

	public Material getColor() {
		return color;
	}

	@Override
	public String toString() {
		return "Order [user=" + user + ", amount=" + amount + ", color=" + color + "]";
	}

}
