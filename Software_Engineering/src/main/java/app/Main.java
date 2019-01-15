package app;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import factory.shared.FactoryApplication;

/**
 * Simple entry point for the application -- starts GUI, monitoring system and with it the rest of the factory.
 */
class Main {
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		FactoryApplication factory = new FactoryApplication();
		factory.start();
	}
	
}
