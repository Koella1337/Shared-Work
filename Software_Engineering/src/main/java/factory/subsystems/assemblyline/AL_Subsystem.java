package factory.subsystems.assemblyline;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


import org.apache.derby.impl.tools.ij.Main;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import factory.shared.AbstractSubsystem;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.Utils;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.warehouse.StorageSite;

@SuppressWarnings("unused")
public class AL_Subsystem extends AbstractSubsystem implements Monitorable, Stoppable{
	public AssemblyLine[] al = new AssemblyLine[6];
	private int[] task = new int[6]; 

	
	//This is a test only for the subsystem
	//The notify method also has to be overriden for the test
	
//	public static void main(String[] args) {
//		MonitoringInterface test = null;
//		AL_Subsystem testcase = new AL_Subsystem(test);
//		for(int i=0;i<testcase.al.length;i++) {
//			Position pos = new Position(new Random().nextInt(1000),new Random().nextInt(1000));
//			testcase.al[i] = new AssemblyLine(pos, testcase);
//		}
//		testcase.addTask(Material.COLOR_BLACK, 300);
//		
//	}
//	@Override
//	public void notify(FactoryEvent e) {
//		System.out.println(e.getKind());
//	}
	
	public AL_Subsystem(MonitoringInterface monitor, Element xmlAL) {
		super(monitor);
		Objects.requireNonNull(xmlAL);
		
		NodeList alNodes = xmlAL.getElementsByTagName("assemblyline");
		for (int i = 0; i < al.length; i++) {
			Position pos = Utils.xmlGetPositionFromElement((Element) alNodes.item(i));
			Element asLi = (Element) alNodes.item(i);
			String direction = asLi.getElementsByTagName("direction").item(0).getTextContent();
			Material color;
			switch(i) {
			case 0:
				color = Material.COLOR_BLACK;
				break;
			case 1:
				color = Material.COLOR_BLUE;
				break;
			case 2:
				color = Material.COLOR_GRAY;
				break;
			case 3:
				color = Material.COLOR_GREEN;
				break;
			case 4:
				color = Material.COLOR_RED;
				break;
			case 5:
				color = Material.COLOR_WHITE;
				break;
			default:
				color = null;
				break;
			}
			al[i] = new AssemblyLine(pos, this, textToDirection(direction), color);
		}
	}
	
	private int textToDirection(String dir) {
		if(dir == "+x") {
			return 1;
		} else return -1;
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
		start();
	}
	
	

	public void draw(Graphics g) {
		for(AssemblyLine a: al) {
			a.draw(g);
		}
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

	public void addBox(Container box) { //You can't add a box to the general subsystem
	}
	
	public void restart() { //Everything just got fixed... magically
		for(AssemblyLine a: al) {
			a.restart();
		}
	}




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



	@Override
	public List<Placeable> getPlaceables() {
		List<Placeable> plc = new ArrayList<Placeable>();
		for(AssemblyLine a: al) {
			if(a != null) {
				plc.addAll(a.getPlaceables());
			}
			
		}
		return plc;
	}

}