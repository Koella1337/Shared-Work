package app.model.car;

import static app.model.car.CarUtils.CAR_STAT_MAX_POINTS;

/**
 * A {@link Car} has 15 points that it can put into different CarStats.<br>
 * Investing points into a CarStat increases its value. The purpose of the value depends on the implementation.
 */
public abstract class CarStat {
	
	private final short points;
	
	public CarStat(short points) {
		if (points < 0 || points > CAR_STAT_MAX_POINTS) {
			throw new IllegalArgumentException("Can not invest less than 0 or more than "+CAR_STAT_MAX_POINTS+" points into a CarStat.");
		}
		this.points = points;
	}
	
	public short getPoints() {
		return points;
	}
	
	public abstract double getValue();
	
}
