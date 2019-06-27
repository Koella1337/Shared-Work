package app.model.car;

import app.model.OilSpill;

/**
 * Holds constants and other utilities (e.g. {@link CarStat}s) for {@link Car}s.
 */
public class CarUtils {

	/** The amount of stats that can be invested into a single stat at maximum. */
	public static final int CAR_STAT_MAX_POINTS = 10;
	
	/** The total amount of points a car must put into its stats. */
	public static final int CAR_STAT_POINTS = 15;
	
	public static final double CAR_X_SIZE = 16;
	public static final double CAR_Y_SIZE = 10;
	
	//----------------------------------- Only CarStats below this point. -----------------------------------
	
	public static class MaxSpeed extends CarStat {

		public MaxSpeed(int points) {
			super(points);
		}

		/**
		 * The value of this stat defines the maximum speed (pixels/update) with which a car can drive.<br>
		 * The current speed of a car should not exceed this value.
		 */
		@Override
		public double getValue() {
			// TODO 
			return 100;
		}
		
	}
	
	public static class Acceleration extends CarStat {

		public Acceleration(int points, MaxSpeed maxSpeed) {
			super(points);
		}

		/**
		 * The value of this stat defines how quickly a car can reach its maximum speed.<br>
		 * Add this value after every update to the current speed until the car reached its maximum speed.
		 */
		@Override
		public double getValue() {
			// TODO 
			return 0;
		}
		
	}
	
	public static class Stability extends CarStat {

		public Stability(int points) {
			super(points);
		}
		
		/**
		 * The value of this stat defines how likely (in percent: 0 to 100) a car is to skid on an {@link OilSpill}.
		 */
		@Override
		public double getValue() {
			// TODO 
			return 0;
		}
		
	}
	
}
