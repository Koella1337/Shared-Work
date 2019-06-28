package app.model.car;

import static app.model.SimulationConstants.GOAL_X_POS;
import static app.model.car.CarUtils.CAR_STAT_MAX_POINTS;
import static app.model.car.CarUtils.CAR_STAT_POINTS;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import app.model.Transform;
import app.model.car.CarUtils.Acceleration;
import app.model.car.CarUtils.MaxSpeed;
import app.model.car.CarUtils.Stability;
import app.timer.Updateable;

public class Car implements Updateable {

	private static final Random RNG = new Random();
	private static final AtomicInteger NEXT_CAR_ID = new AtomicInteger();
	
	public static Car createRandomCar(Transform carTransform) {
		Color carColor = new Color(RNG.nextInt(256), RNG.nextInt(256), RNG.nextInt(256));
		
		int pointsLeft = CAR_STAT_POINTS;
		int maxSpeedPoints = RNG.nextInt(1 + CAR_STAT_MAX_POINTS);
		
		pointsLeft -= maxSpeedPoints;
		int accelerationPoints = RNG.nextInt(1 + (pointsLeft < CAR_STAT_MAX_POINTS ? pointsLeft : CAR_STAT_MAX_POINTS));
		
		pointsLeft -= accelerationPoints;
		int stabilityPoints;
		
		if (pointsLeft > CAR_STAT_MAX_POINTS) {
			stabilityPoints = CAR_STAT_MAX_POINTS;
			pointsLeft -= stabilityPoints;
			
			int pointsLeftHalved = pointsLeft / 2;
			maxSpeedPoints += pointsLeftHalved;
			accelerationPoints += pointsLeftHalved;
			
			if (pointsLeft % 2 == 1) { //pointsLeft was odd
				if (RNG.nextBoolean()) {
					maxSpeedPoints++;
				} else {
					accelerationPoints++;
				}
			}
		} else {
			stabilityPoints = pointsLeft;
		}
		
		return new Car(carTransform, carColor, maxSpeedPoints, accelerationPoints, stabilityPoints);
	}
	
	private final int id;
	private final Color color;
	
	private final MaxSpeed maxSpeed;
	private final Acceleration acceleration;
	private final Stability stability;
	
	private Transform transform;
	private boolean isFinished;
	private boolean isCrashed;
	
	/** Current speed in pixels/update. */
	private double currentSpeed;
	
	public Car(Transform transform, Color color, int maxSpeedPoints, int accelerationPoints, int stabilityPoints) {
		if ((maxSpeedPoints + accelerationPoints + stabilityPoints) != CAR_STAT_POINTS) {
			throw new IllegalArgumentException(
				"The stats invested into this car do not match up. "
				+ "Expected: " +CAR_STAT_POINTS+", Actual:" + (maxSpeedPoints + accelerationPoints + stabilityPoints)
			);
		}
		
		this.id = NEXT_CAR_ID.getAndIncrement();
		this.color = color;
		
		this.maxSpeed = new MaxSpeed(maxSpeedPoints);
		this.acceleration = new Acceleration(accelerationPoints);
		this.stability = new Stability(stabilityPoints);
		
		this.transform = transform;
		this.currentSpeed = 0;
	}
	
	@Override
	public String toString() {
		return "[Car [color=" + color + ", maxSpeed=" + maxSpeed + ", acceleration=" + acceleration + ", stability="
				+ stability + ", transform=" + transform + ", isFinished=" + isFinished + ", isCrashed=" + isCrashed
				+ ", currentSpeed=" + currentSpeed + "]]";
	}

	@Override
	public void update() {
		if (!(isCrashed || isFinished)) {
			if ((RNG.nextDouble() * 100) < stability.getValue()) {
				isCrashed = true;
				return;
			}
			
			transform.setXPos(Math.min(transform.getXPos() + currentSpeed, GOAL_X_POS));
			currentSpeed = Math.min(currentSpeed + acceleration.getValue(), maxSpeed.getValue());
			
			currentSpeed *= (0.95 + RNG.nextDouble() * 0.1);
			
			if (GOAL_X_POS == transform.getXPos()) {
				isFinished = true;
			}
		}
	}
	
	public void reset() {
		isCrashed = false;
		isFinished = false;
		currentSpeed = 0;
	}
	
	//------------------------------------------- Only Getters & Setters below this point -------------------------------------------
	
	public int getId() {
		return id;
	}
	
	public Color getColor() {
		return color;
	}

	public MaxSpeed getMaxSpeed() {
		return maxSpeed;
	}

	public Acceleration getAcceleration() {
		return acceleration;
	}

	public Stability getStability() {
		return stability;
	}

	public Transform getTransform() {
		return transform;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public boolean isCrashed() {
		return isCrashed;
	}

	public double getCurrentSpeed() {
		return currentSpeed;
	}
	
}
