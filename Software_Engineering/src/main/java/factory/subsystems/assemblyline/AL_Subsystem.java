package factory.subsystems.assemblyline;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import factory.shared.AbstractSubsystem;
import factory.shared.Container;
import factory.shared.Position;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public class AL_Subsystem extends AbstractSubsystem implements Monitorable, Stoppable{
	public AssemblyLine[] al = new AssemblyLine[6];
	private int[] task = new int[6]; 

	public AL_Subsystem(MonitoringInterface monitor) {
		super(monitor);
		
		for(int i=0;i<al.length;i++) {
			Position pos = new Position(new Random().nextInt(1000),new Random().nextInt(1000));
			al[i] = new AssemblyLine(pos, this);
		}
		
		
		
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
	
	public void fixBroken() { //Everything just got fixed... magically
		for(AssemblyLine a: al) {
			a.fixBroken();
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