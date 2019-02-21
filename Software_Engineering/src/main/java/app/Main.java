package app;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import factory.shared.FactoryApplication;

/**
 * Simple entry point for the application -- starts GUI, monitoring system and with it the rest of the factory.
 */
class Main {
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		File xmlLayoutFile;
		if (args.length > 0) {
			xmlLayoutFile = new File(args[0] /*path to xml*/);
		}
		else {
			xmlLayoutFile = new File("resources/factory_layout.xml");
		}
		FactoryApplication factory = new FactoryApplication(xmlLayoutFile);
		factory.start();
	}
	
}
