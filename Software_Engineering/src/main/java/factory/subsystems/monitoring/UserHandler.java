package factory.subsystems.monitoring;

import factory.shared.Constants;
import factory.subsystems.monitoring.onlineshop.OnlineShopUser;

public class UserHandler {

	public boolean loginCorrect(OnlineShopUser user) {
		return Constants.testUserName.equals(user.getName()) && Constants.testPassword.equals(user.getPassword());
	}

}
