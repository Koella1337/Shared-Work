package app.model.car;

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

		private final double value;
		
		public MaxSpeed(int points) {
			super(points);
			this.value = 1.8 + ((double) points / CAR_STAT_MAX_POINTS) * 1.2;
		}

		/**
		 * The value of this stat defines the maximum speed (pixels/update) with which a car can drive.<br>
		 * The current speed of a car should not exceed this value.
		 */
		@Override
		public double getValue() {
			return value;
		}
		
	}
	
	public static class Acceleration extends CarStat {

		private final double value;
		
		public Acceleration(int points) {
			super(points);
			this.value = 0.01 + ((double) points / CAR_STAT_MAX_POINTS) * 0.008;
		}

		/**
		 * The value of this stat defines how quickly a car can reach its maximum speed.<br>
		 * Add this value after every update to the current speed until the car reached its maximum speed.
		 */
		@Override
		public double getValue() {
			return value;
		}
		
	}
	
	public static class Stability extends CarStat {

		private final double value;
		
		public Stability(int points) {
			super(points);
			this.value = 0.1 - ((double) points / CAR_STAT_MAX_POINTS) * 0.099999;
		}
		
		/**
		 * The value of this stat defines how likely (in percent/update) a car is to stop working.
		 */
		@Override
		public double getValue() {
			return value;
		}
		
	}
	
}
