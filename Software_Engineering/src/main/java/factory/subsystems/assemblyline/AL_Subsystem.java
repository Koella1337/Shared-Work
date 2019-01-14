package factory.subsystems.assemblyline;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.List;

import factory.shared.Position;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.*;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.assemblyline.interfaces.RobotInterface;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.lang.model.element.Element;
import javax.xml.parsers.DocumentBuilder;

public class AL_Subsystem extends AbstractSubsystem implements Monitorable, Stoppable{
	public AssemblyLine[] al = new AssemblyLine[6];
	public int[] task = new int[6]; 

	public AL_Subsystem(MonitoringInterface monitor) {
		super(monitor);
//		try {
//			Document layoutDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("resources/factory_layout.xml"));
//			
//			Element al1 = (Element) layoutDoc.getElementsByTagName("assemblylines").item(0);
//			
//		} catch (SAXException | IOException | ParserConfigurationException e1) {
//			e1.printStackTrace();
//		}
		
		//TODO
		//ADD ASSEMBLYLINES
	}

	/**
	 * 
	 * @param color of the cars
	 * @param quantity of the cars
	 */
	public void addTask(Material color, int q) {
		switch (color) {
		case CAR:
			break;
		case CAR_BODIES:
			break;
		case COLOR_BLACK:
			task[0] = q;
			break;
		case COLOR_BLUE:
			task[1] = q;
			break;
		case COLOR_GRAY:
			task[2] = q;
			break;
		case COLOR_GREEN:
			task[3] = q;
			break;
		case COLOR_RED:
			task[4] = q;
			break;
		case COLOR_WHITE:
			task[5] = q;
			break;
		case LUBRICANT:
			break;
		case SCREWS:
			break;
		case WHEELS:
			break;
		default:
			break;
			
		}
	}
	
	

	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		for(int i = 0; i < 6; i++) {
			al[i].start(task[i]);
		}
	}
	
	public void stopProduction(Material color) {
		switch (color) {
		case CAR:
			break;
		case CAR_BODIES:
			break;
		case COLOR_BLACK:
			al[0].stop();
			break;
		case COLOR_BLUE:
			al[1].stop();
			break;
		case COLOR_GRAY:
			al[2].stop();
			break;
		case COLOR_GREEN:
			al[3].stop();
			break;
		case COLOR_RED:
			al[4].stop();
			break;
		case COLOR_WHITE:
			al[5].stop();
			break;
		case LUBRICANT:
			break;
		case SCREWS:
			break;
		case WHEELS:
			break;
		default:
			break;
			
		}
	}

	@Override
	public void stop() {
		for(int i = 0; i < 6; i++) {
			((Stoppable) al[i]).stop();
		}
		
	}

	public void addBox(Container box) {
		// TODO Auto-generated method stub
		
	}


//	@Override
//	public void notifyMonitoringSystem(Task task, RobotEvent event) {
//		// TODO Auto-generated method stub
//		
//	}




	@Override
	public SubsystemStatus getStatus() {
		SubsystemStatus status = SubsystemStatus.WAITING;
		for(int i = 0; i < 6; i++) {
			if(al[i].status() == SubsystemStatus.BROKEN) status = SubsystemStatus.BROKEN;
			if(al[i].status() == SubsystemStatus.STOPPED && status == SubsystemStatus.WAITING) status = SubsystemStatus.STOPPED;
			if(al[i].status() == SubsystemStatus.RUNNING && status == SubsystemStatus.WAITING) status = SubsystemStatus.RUNNING;
		}
		return status;
	}

	
	public void notify(FactoryEvent event) {
		super(event);
	}



	@Override
	public List<Placeable> getPlaceables() {
		// TODO Auto-generated method stub
		return null;
	}

}