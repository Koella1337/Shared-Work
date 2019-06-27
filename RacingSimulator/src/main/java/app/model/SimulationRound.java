package app.model;

import static app.model.SimulationConstants.CAR_AMOUNT;
import static app.model.SimulationConstants.START_X_POS;
import static app.model.SimulationConstants.TRACK_HEIGHT;
import static app.model.car.CarUtils.CAR_X_SIZE;
import static app.model.car.CarUtils.CAR_Y_SIZE;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import app.model.car.Car;
import app.timer.Updateable;

public class SimulationRound implements Updateable {
	
	private final List<? extends Car> cars;
	
	private AtomicInteger nextPlace = new AtomicInteger(1);
	private final ConcurrentMap<Integer, Car> placements = new ConcurrentHashMap<>();
	
	public SimulationRound(List<? extends Car> carsFromPreviousRound) {
		this.cars = prepareCarsForNextRound(carsFromPreviousRound);
		System.out.println("[=======================]");
		System.out.println("[     ROUND STARTED     ]");
		System.out.println("[=======================]");
	}
	
	@Override
	public void update() {
		cars.parallelStream().forEach(car -> {
			//TODO: collision detection with OilSpill
			
			car.update();
			if (car.isFinished() && nextPlace.get() < SimulationConstants.WINNER_AMOUNT) {
				placements.put(nextPlace.getAndIncrement(), car);
			}
		});
	}
	
	public List<? extends Car> getCars() {
		return cars;
	}
	
	/**
	 * @return A list of cars sorted by their placement.<br>
	 * The maximum amount of cars able to score a placement is defined in {@link SimulationConstants#WINNER_AMOUNT}
	 */
	public List<? extends Car> getPlacements() {
		return placements.entrySet()
			.stream()
			.sorted((entry1, entry2) -> {
				return entry1.getKey().compareTo(entry2.getKey());
			})
			.map(entry -> entry.getValue())
			.collect(Collectors.toList());
	}
	
	public boolean isFinished() {
		return cars.stream().allMatch(car -> (car.isCrashed() || car.isFinished()));
	}
	
	//----------------------------------------- Only private methods after this point -----------------------------------------
	
	private Stream<? extends Car> generateCars(int amountToGenerate) {
		return Stream.generate(() -> {
			return Car.createRandomCar(new Transform(0, 0, CAR_X_SIZE, CAR_Y_SIZE));
		}).limit(amountToGenerate);
	}
	
	private List<? extends Car> prepareCarsForNextRound(List<? extends Car> carsFromPreviousRound) {
		Stream<? extends Car> cars;
		
		if (carsFromPreviousRound == null) {
			cars = generateCars(CAR_AMOUNT);
		} else {
			cars = Stream.concat(
				carsFromPreviousRound.stream().peek(car -> car.reset()), 
				generateCars(CAR_AMOUNT - carsFromPreviousRound.size())
			);
		}
		
		double[] previousCarYPos = {0};		//need reference for lambda
		return cars.peek(car -> {
			car.getTransform().setXPos(START_X_POS);
			
			double yPos = previousCarYPos[0] + (double) TRACK_HEIGHT * 0.95 / CAR_AMOUNT;
			car.getTransform().setYPos(yPos);
			
			previousCarYPos[0] = yPos;
		}).collect(Collectors.toList());
	}

}
