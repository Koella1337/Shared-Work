package factory.shared;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import app.gui.UIConfiguration;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.assemblyline.AssemblyLineSystem;
import factory.subsystems.monitoring.MonitoringSystem;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.warehouse.WarehouseSystem;

public class FactoryApplication implements Stoppable {

	private MonitoringInterface monitor;

	public FactoryApplication(File xmlLayoutFile) throws SAXException, IOException, ParserConfigurationException {
		Document layoutDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlLayoutFile);
		initFactoryFromXML(layoutDoc);
	}

	private void initFactoryFromXML(Document layoutDoc) {
		Element factory = (Element) layoutDoc.getElementsByTagName("factory").item(0);
		List<Placeable> agvAccessiblePlaceables = new ArrayList<>();
		
		UIConfiguration config = getUiConfigFromFactoryElement(factory);
		this.monitor = new MonitoringSystem(config);

		Element warehousesystem = (Element) (factory).getElementsByTagName("warehouse").item(0);
		WarehouseSystem whs = new WarehouseSystem(getMonitor(), (Element) warehousesystem);
		this.monitor.setWarehouseSystem(whs);
		agvAccessiblePlaceables.addAll(whs.getOutputBoxes());

		Element assemblyLines = (Element) (factory).getElementsByTagName("assemblylines").item(0);
		
		AssemblyLineSystem alSystem = new AssemblyLineSystem(this.monitor, assemblyLines);
		this.monitor.setAssemblyLine(alSystem); //TODO check if it works -Max
		agvAccessiblePlaceables.addAll(alSystem.getAgvAccessiblePlaceables());
		
		//		OnlineShopUser user = new OnlineShopUser("thomas");
		//		Order order = new Order(user, 4);
		//		this.monitor.addOrder(order);
		
		AgvCoordinator agvSystem = new AgvCoordinator(this.monitor, factory, agvAccessiblePlaceables);
		this.monitor.setAgvSystem(agvSystem);
		
		this.monitor.setShippingBox(Utils.xmlGetPositionFromFirstChild(factory, "shippingbox"));
		this.monitor.setStaffQuarterPosition(Utils.xmlGetPositionFromFirstChild(factory, "staffquarter"));
	}

	private UIConfiguration getUiConfigFromFactoryElement(Element factory) {
		Element sizeElem = (Element) factory.getElementsByTagName("size").item(0);
		int[] size = Arrays.stream(sizeElem.getTextContent().split(",")).mapToInt(str -> Integer.parseInt(str.trim())).toArray();
		
		UIConfiguration config = new UIConfiguration(size[0],size[1]);
		return config;
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
