package app.model.car;

import static app.model.SimulationConstants.GOAL_X_POS;
import static app.model.car.CarUtils.CAR_STAT_MAX_POINTS;
import static app.model.car.CarUtils.CAR_STAT_POINTS;

import java.awt.Color;
import java.util.Random;

import app.model.Transform;
import app.model.car.CarUtils.Acceleration;
import app.model.car.CarUtils.MaxSpeed;
import app.model.car.CarUtils.Stability;
import app.timer.Updateable;

public class Car implements Updateable {

	public static Car createRandomCar(Transform carTransform) {
		Random rng = new Random();
		Color carColor = new Color(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
		
		int pointsLeft = CAR_STAT_POINTS;
		int maxSpeedPoints = rng.nextInt(1 + CAR_STAT_MAX_POINTS);
		
		pointsLeft -= maxSpeedPoints;
		int accelerationPoints = rng.nextInt(1 + (pointsLeft < CAR_STAT_MAX_POINTS ? pointsLeft : CAR_STAT_MAX_POINTS));
		
		pointsLeft -= accelerationPoints;
		int stabilityPoints;
		
		if (pointsLeft > CAR_STAT_MAX_POINTS) {
			stabilityPoints = CAR_STAT_MAX_POINTS;
			pointsLeft -= stabilityPoints;
			
			int pointsLeftHalved = pointsLeft / 2;
			maxSpeedPoints += pointsLeftHalved;
			accelerationPoints += pointsLeftHalved;
			
			if (pointsLeft % 2 == 1) { //pointsLeft was odd
				if (rng.nextBoolean()) {
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
			transform.setXPos(Math.min(transform.getXPos() + currentSpeed, GOAL_X_POS));
			currentSpeed = Math.min(currentSpeed + acceleration.getValue(), maxSpeed.getValue());
			
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
	
	public void collideWithOil() {
		//TODO: crash depending on Stability
		
		isCrashed = false;
	}
	
	//------------------------------------------- Only Getters & Setters below this point -------------------------------------------
	
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
	
}
