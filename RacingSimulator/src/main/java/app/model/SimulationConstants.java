package app.model;

import app.model.car.CarUtils;

public class SimulationConstants {

	/** Changing this value changes the speed of the simulation: how quickly cars drive, ... */
	public static final int SIMULATION_UPDATES_PER_SECOND = 60;
	
	public static final int CAR_AMOUNT = 70;
	public static final int WINNER_AMOUNT = 10;
	
	public static final int TRACK_HEIGHT = 1000;
	public static final int TRACK_WIDTH = 1000;
	
	public static final int START_X_POS = (int) CarUtils.CAR_X_SIZE * 2;
	public static final int GOAL_X_POS = TRACK_WIDTH - (int) CarUtils.CAR_X_SIZE * 3;
	
}
