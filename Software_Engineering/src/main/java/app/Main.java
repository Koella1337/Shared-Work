package app;

import factory.shared.FactoryApplication;
import factory.shared.FactoryEvent;
import factory.shared.enums.EventKind;
import factory.subsystems.agv.AgvCoordinator;

/**
 * Simple entry point for the application -- starts GUI, monitoring system and with it the rest of the factory.
 */
class Main {
	
	public static void main(String[] args) {
		FactoryApplication factory = new FactoryApplication();
		factory.start();
		
		try {
			Thread.sleep(10000);
			System.out.println("ERROR automatically created");
			factory.getMonitor().handleEvent(new FactoryEvent(new AgvCoordinator(factory.getMonitor()), EventKind.MONITORING_HANDLE_EVENT_FAILED));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}
	
}
