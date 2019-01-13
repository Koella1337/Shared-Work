package factory.shared;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import factory.shared.interfaces.Stoppable;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.agv.Forklift;
import factory.subsystems.assemblyline.AssemblyLine;
import factory.subsystems.monitoring.MonitoringSystem;
import factory.subsystems.monitoring.TestAGVCoord;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.warehouse.WarehouseSystem;

public class FactoryApplication implements Stoppable {

	private MonitoringInterface monitor;

	public FactoryApplication() throws SAXException, IOException, ParserConfigurationException {
		this.monitor = new MonitoringSystem();

	
	//	this.monitor.setCurrentSubsystemToShow(testSubsystem1);

		Document layoutDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new File("resources/factory_layout.xml"));
		initFactoryFromXML(layoutDoc);
	}

	private void initFactoryFromXML(Document layoutDoc) {
		Element factory = (Element) layoutDoc.getElementsByTagName("factory").item(0);

		Element warehousesystem = (Element) (factory).getElementsByTagName("warehouse").item(0);
		WarehouseSystem whs = new WarehouseSystem(getMonitor(), (Element) warehousesystem);
		this.monitor.setWarehouseSystem(whs);

		Element assemblyLines = (Element) (factory).getElementsByTagName("assemblylines").item(0);
		//TODO @thomas create assembly line
		//this.monitor.addToSubsystemList(assemblyLine);
		
		Element forklists = (Element) (factory).getElementsByTagName("forklifts").item(0);
		AgvCoordinator agvSystem = new TestAGVCoord(this.monitor); //TODO @thomas add xmlelement to constructor
		this.monitor.setAgvSystem(agvSystem);
		
		agvSystem.addForklift(new Forklift(new Position(100,100)));
		
		AssemblyLine assemblyLine = new AssemblyLine(null,null);
		this.monitor.setAssemblyLine(assemblyLine);
		
		addShippingBoxToMonitoring(factory);
//		OnlineShopUser user = new OnlineShopUser("thomas");
//		Order order = new Order(user, 4);
//		this.monitor.addOrder(order);
	}
	
	private void addShippingBoxToMonitoring(Element factory) {
		Position shippingBoxPosition = Utils.xmlGetPositionFromFirstChild(factory, "shippingbox");
		
		ResourceBox shippingBox = new ResourceBox(shippingBoxPosition);
		
		this.monitor.setShippingBox(shippingBox);
	}

	
	public void start() {
		this.monitor.start();
	}

	public void stop() {
		this.monitor.start();
	}

	public MonitoringInterface getMonitor() {
		return monitor;
	}

}
