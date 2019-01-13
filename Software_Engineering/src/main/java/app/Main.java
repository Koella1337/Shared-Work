package app;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import factory.shared.FactoryApplication;
import factory.shared.FactoryEvent;
import factory.shared.enums.EventKind;
import factory.subsystems.agv.AgvCoordinator;

/**
 * Simple entry point for the application -- starts GUI, monitoring system and with it the rest of the factory.
 */
class Main {
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
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
