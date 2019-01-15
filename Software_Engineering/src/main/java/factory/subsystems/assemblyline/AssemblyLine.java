package factory.subsystems.assemblyline;
import java.awt.Graphics;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.derby.impl.tools.ij.util;

import app.gui.SubsystemMenu;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.enums.MaterialStatus;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.assemblyline.interfaces.RobotInterface;

@SuppressWarnings("unused")
public class AssemblyLine implements Monitorable, RobotInterface, Stoppable, Placeable, ContainerDemander {
	private Robot[] robots = new Robot[4];
	private Conveyor conveyor;
	private Position position;
	private AL_Subsystem alsubsys;
	private int finished;
	private SubsystemStatus alstatus;
	private Material color;
	
	
	public AssemblyLine(Position pos, AL_Subsystem al, int direction, Material c) {
		position = pos;
		this.color = c;
		this.alsubsys = al;
		this.alsubsys.al[0] = this;
		Position rpos = position;
		if(direction > 0) {
			
			robots[0] = new Robot(this, rpos, direction, RobotTypes.GRABBER, null, 0); //Create 4 robots
			rpos.xPos += (350/4) * direction;
			robots[1] = new Robot(this, rpos, direction, RobotTypes.SCREWDRIVER, Material.SCREWS, 100);
			rpos.xPos += (350/4) * direction;
			robots[2] = new Robot(this, rpos, direction, RobotTypes.PAINTER, color,  100);
			rpos.xPos += (350/4) * direction;
			robots[3] = new Robot(this, rpos, direction, RobotTypes.INSPECTOR, null, 0);
		} else {
			rpos.xPos -= (350/4);
			robots[0] = new Robot(this, rpos, direction, RobotTypes.GRABBER, null, 0); //Create 4 robots
			rpos.xPos -= (350/4);
			robots[1] = new Robot(this, rpos, direction, RobotTypes.SCREWDRIVER, Material.SCREWS, 100);
			rpos.xPos -= (350/4) * direction;
			robots[2] = new Robot(this, rpos, direction, RobotTypes.PAINTER, color,  100);
			rpos.xPos -= (350/4) * direction;
			robots[3] = new Robot(this, rpos, direction, RobotTypes.INSPECTOR, null, 0);
			
		}
		
		if(direction > 0) {
			rpos = position;
		}
		rpos.yPos -= 60;
		conveyor = new Conveyor(this, rpos, direction, 20, 100); //Create conveyor
	}
	
	public Robot[] getRobots() {
		return robots;
	}
	
	public void addBox(Container box) { //Adds the box to the matching robot/conveyor
		for(Robot r: robots) {
			if(r.material == box.getMaterial()) r.addBox(box);
		}
		if(box.getMaterial() == Material.LUBRICANT) conveyor.addBox(box);
	}
	


	@Override
	public int getMaterials() {
		return -1;
	}
	
	@Override
	public SubsystemStatus status() {
		SubsystemStatus status = SubsystemStatus.WAITING;
		for(Robot r: robots) {
			if(r.status() == SubsystemStatus.RUNNING && status == SubsystemStatus.WAITING) {
				status = r.status();
			}
			if(r.status() == SubsystemStatus.STOPPED && status != SubsystemStatus.BROKEN) {
				status = r.status();
			}
			if(r.status() == SubsystemStatus.BROKEN) {
				status = r.status();
			}
			System.out.println(r.status());
		}
		if(conveyor.status() == SubsystemStatus.RUNNING && status == SubsystemStatus.WAITING) {
			status = conveyor.status();
		}
		if(conveyor.status() == SubsystemStatus.STOPPED && status != SubsystemStatus.BROKEN) {
			status = conveyor.status();
		}
		if(conveyor.status() == SubsystemStatus.BROKEN) {
			status = conveyor.status();
		}
		
		return status;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void draw(Graphics g) {
		for(Robot r: robots) {
			r.draw(g);
		}
		conveyor.draw(g);
	}
	

	public void start(int q) {
		double speed = q/10; //Adaptive speed
		if(speed > 30) speed = 30; else if(speed < 10) speed = 10; //Boundaries for the speed
		conveyor.setSpeed(speed);
		while (alstatus == SubsystemStatus.RUNNING) {
			
			for(Robot r: robots) {
				r.start();
			}
			while(notReady()) { //Waiting for the robots
			}
			
			
			while(conveyor.status() != SubsystemStatus.WAITING) { //Waiting for the conveyor
			}
		}
		FactoryEvent taskDone = new FactoryEvent(this, EventKind.TASK_FINISHED);
		this.notify(taskDone);
	}
	
	
	private boolean notReady() {
		for(Robot r: robots) {
			if(r.status() != SubsystemStatus.WAITING) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void stop() {
		for(Robot r: robots) {
			r.stop();
		}
		conveyor.stop();
		alstatus = SubsystemStatus.STOPPED;
	}


	@Override
	public void receiveContainer(Container box) {
		for(Robot r: robots) {
			if(r.material == box.getMaterial()) r.addBox(box);
		}
		if(box.getMaterial() == Material.LUBRICANT) conveyor.addBox(box);
	}

	@Override
	public String getName() {
		return "Assembly Line with color " + color;
	}



	@Override
	public List<Placeable> getPlaceables() {
		List<Placeable> plc = new ArrayList<Placeable>();
		for(Robot r: robots) {
			plc.add(r);
		}
		plc.add(conveyor);
		return plc;
	}

	@Override
	public SubsystemMenu getCurrentSubsystemMenu() {
		return null;
	}
	
	public void stoppedSys(Object source, FactoryEvent event) {
		this.notify(new FactoryEvent(this, EventKind.CAR_FINISHED));
	}



	@Override
	public SubsystemStatus getStatus() { //This should only be used for test cases
		SubsystemStatus alstatus = SubsystemStatus.WAITING;
		for(Robot r: robots) {
			if(r.status() == SubsystemStatus.RUNNING && alstatus == SubsystemStatus.WAITING) {
				alstatus = r.status();
			}
			if(r.status() == SubsystemStatus.STOPPED && alstatus != SubsystemStatus.BROKEN) {
				alstatus = r.status();
			}
			if(r.status() == SubsystemStatus.BROKEN) {
				alstatus = r.status();
			}
		}
		return alstatus;
	}

	@Override
	public void start() {
		alstatus = SubsystemStatus.RUNNING;
		start(0);
	}

	@Override
	public void notify(FactoryEvent event) {
		if(event.getKind() == EventKind.CAR_FINISHED) {
			finished++;
			Material car;
			switch(color) {
			case COLOR_BLACK:
				car = Material.CAR_BLACK;
				break;
			case COLOR_BLUE:
				car = Material.CAR_BLUE;
				break;
			case COLOR_GRAY:
				car = Material.CAR_GRAY;
				break;
			case COLOR_GREEN:
				car = Material.CAR_GREEN;
				break;
			case COLOR_RED:
				car = Material.CAR_RED;
				break;
			case COLOR_WHITE:
				car = Material.CAR_WHITE;
				break;
			default:
				car = Material.CAR_BLACK;
				break;
			}
			conveyor.getOutputbox().receiveContainer(new Container(car));
			if(conveyor.getOutputbox().getFullness() == MaterialStatus.BAD) {
				FactoryEvent full = new FactoryEvent(getALSys(), EventKind.RESOURCEBOX_ALMOST_FULL, car, conveyor.getOutputbox());
			}
		}
		alsubsys.notify(event);
	}
	
	public AL_Subsystem getALSys() {
		return alsubsys;
	}
	
	public void restart() {
		for(Robot r: robots) {
			r.restart();
		}
		conveyor.restart();
		start();
	}

	public Conveyor getConveyor() {
		return conveyor;
	}
	
	public Material getMaterial() {
		return color;
	}
	
	public List<Placeable> getAGVRobot(){
		List<Placeable> plc = new ArrayList<Placeable>();
		for(Robot r: robots) {
			if(r.robot == RobotTypes.PAINTER || r.robot == RobotTypes.SCREWDRIVER) {
				plc.add(r);
			}
		}
		return plc;
	}
	
	public List<Placeable> getAGVConveyor(){
		List<Placeable> plc = new ArrayList<Placeable>();
		plc.add(conveyor);
		return plc;
	}
	
	public List<Placeable> getAGVOutputbox(){
		List<Placeable> plc = new ArrayList<Placeable>();
		plc.add(conveyor.getOutputbox());
		return plc;
	}


}