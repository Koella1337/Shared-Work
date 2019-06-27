package app.model.car;

import static app.model.car.CarUtils.CAR_STAT_MAX_POINTS;

/**
 * A {@link Car} has 15 points that it can put into different CarStats.<br>
 * Investing points into a CarStat increases its value. The purpose of the value depends on the implementation.
 */
public abstract class CarStat {
	
	private final int points;
	
	public CarStat(int points) {
		if (points < 0 || points > CAR_STAT_MAX_POINTS) {
			throw new IllegalArgumentException("Can not invest less than 0 or more than "+CAR_STAT_MAX_POINTS+" points into a CarStat.");
		}
		this.points = points;
	}
	
	public int getPoints() {
		return points;
	}
	
	public String getStatName() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public String toString() {
		return String.format("[%s (%d/%d): %.2f]", 
			getClass().getSimpleName(), 
			getPoints(), 
			CAR_STAT_MAX_POINTS, 
			getValue()
		);
	}
	
	public abstract double getValue();
	
}
