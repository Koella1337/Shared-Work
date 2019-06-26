package app.model;

import static app.model.SimulationConstants.FOOD_HUNT_TIMEOUT;
import static app.model.SimulationConstants.FOOD_HUNT_TIMEOUT_UNIT;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class LivingCircle implements Entity {

	@Override
	public double size() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public Position position() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void eat(double foodValue) {
		// TODO Auto-generated method stub
		
	}
	
	//reacts to Hunt, on success calls growBigger, on failure shrink or die
	
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		var hunt = CompletableFuture.runAsync(new FoodHunt())
			.orTimeout(FOOD_HUNT_TIMEOUT, FOOD_HUNT_TIMEOUT_UNIT);
//			.exceptionally(ex -> {
//				if (ex instanceof TimeoutException) {
//					System.out.println("Timeout");
//				} else if (ex instanceof DeathException) {
//					System.out.println("Death");
//				}
//				System.out.println("aosdkaopsdk: " + ex);
//				return null;
//			});
//		
//		new Thread(() -> {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
////			
//		}).start();
		
		try {
			hunt.completeExceptionally(new DeathException(null));
			hunt.get();
			
			System.out.println("Completed without exception.");
		} catch (ExecutionException ex) {
			System.out.println("Ex: " + ex);
//			if (ex instanceof TimeoutException) {
//				System.out.println("Timeout");
//			} else if (ex instanceof DeathException) {
//				System.out.println("Death");
//			}
		}
		
		while (true) {
			Thread.sleep(1000);
		}
	}
	
	@SuppressWarnings("unused")
	private void growBigger() {
		
		
		
	}
	
	@Override
	public String toString() {
		// TODO 
		return super.toString();
	}

	


	
}
