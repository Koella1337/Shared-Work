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
public class AssemblyLine implements Monitorable, RobotInterface, Stoppable, Placeable, ContainerDemander, ContainerSupplier {
	private Robot[] robots = new Robot[4];
	private Conveyor conveyor;
	private Position position;
	private AL_Subsystem alsys;
	private int finished;
	private SubsystemStatus status;
	private Material color;
	
	
	public AssemblyLine(Position pos, AL_Subsystem al, int direction, Material color) {
		position = pos;
		this.color = color;
		this.alsys = al;
		this.alsys.al[0] = this;
		Position rpos = position;
		robots[0] = new Robot(this, rpos, direction, RobotTypes.GRABBER, null, 0); //Create 4 robots
		rpos.xPos += 10 * direction;
		robots[1] = new Robot(this, rpos, direction, RobotTypes.SCREWDRIVER, Material.SCREWS, 100);
		rpos.xPos += 10 * direction;
		robots[2] = new Robot(this, rpos, direction, RobotTypes.PAINTER, color,  100);
		rpos.xPos += 10 * direction;
		robots[3] = new Robot(this, rpos, direction, RobotTypes.INSPECTOR, null, 0);
		
		if(direction > 0) { //The Conveyor always starts at the left point
			rpos = position;
		}
		rpos.yPos -= 10;
		conveyor = new Conveyor(this, rpos, direction, 20, 100); //Create conveyor
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
		finished = -4;
		double speed = q/10; //Adaptive speed
		if(speed > 30) speed = 30; else if(speed < 10) speed = 10; //Boundaries for the speed
		conveyor.setSpeed(speed);
		
		while (finished < q || status == SubsystemStatus.RUNNING) {
			for(Robot r: robots) {
				r.start();
			}
			while(notReady()) { //Waiting for the robots
			}
			conveyor.start();
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
		status = SubsystemStatus.STOPPED;
	}

	@Override
	public Container deliverContainer(Material material) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
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
		status = SubsystemStatus.RUNNING;
		start(0);
	}

	@Override
	public void notify(FactoryEvent event) {
		if(event.getKind() == EventKind.CAR_FINISHED) {
			finished++;
			//TODO add car to box
			if(conveyor.getOutputbox().getFullness() == MaterialStatus.BAD) {
				FactoryEvent full = new FactoryEvent(getALSys(), EventKind.RESOURCEBOX_ALMOST_FULL, Material.CAR, conveyor.getOutputbox());
			}
		}
		alsys.notify(event);
	}
	
	public AL_Subsystem getALSys() {
		return alsys;
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

	public void setConveyor(Conveyor conveyor) {
		this.conveyor = conveyor;
	}
	
	public Material getMaterial() {
		return color;
	}


}