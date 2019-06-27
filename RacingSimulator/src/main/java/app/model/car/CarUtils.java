package app.model.car;

/**
 * Holds constants and other utilities (e.g. {@link CarStat}s) for {@link Car}s.
 */
public class CarUtils {

	public static final short CAR_STAT_MAX_POINTS = 10;
	
	public static final double CAR_X_SIZE = 10;
	public static final double CAR_Y_SIZE = 10;
	
	//----------------------------------- Only CarStats below this point. -----------------------------------
	
	public static class Speed extends CarStat {

		
		
		public Speed(short points) {
			super(points);
		}

		@Override
		public double getValue() {
			// TODO 
			return 0;
		}
		
	}
	
	public static class Acceleration extends CarStat {

		public Acceleration(short points) {
			super(points);
		}

		@Override
		public double getValue() {
			// TODO 
			return 0;
		}
		
	}
	
	public static class Stability extends CarStat {

		public Stability(short points) {
			super(points);
		}

		@Override
		public double getValue() {
			// TODO 
			return 0;
		}
		
	}
	
}
