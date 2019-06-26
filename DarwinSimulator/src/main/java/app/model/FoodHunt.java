package app.model;

import java.util.concurrent.atomic.AtomicInteger;

public class FoodHunt implements Runnable {

	public AtomicInteger gatheredFood;
	
	@Override
	public void run() {
		try {
			Thread.sleep(6000);
			System.out.println("FoodHunt done");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
