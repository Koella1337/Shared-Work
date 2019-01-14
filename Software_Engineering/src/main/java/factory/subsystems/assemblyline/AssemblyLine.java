package factory.subsystems.assemblyline;
import java.awt.Graphics;
import java.util.List;

import app.gui.SubsystemMenu;
import factory.shared.AbstractSubsystem;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.Task;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.assemblyline.interfaces.RobotInterface;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public class AssemblyLine implements Monitorable, RobotInterface, Stoppable, Placeable, ContainerDemander, ContainerSupplier {
	public Robot[] robots;
	public Conveyor conveyor;
	public Position position;
	
	
	public AssemblyLine(MonitoringInterface monitor, Position pos) {
		super();
		position = pos;
		//TODO create robots & conveyor
	}
	
	public void addBox(Container box) { //Adds the box to the matching robot/conveyor
		for(Robot r: robots) {
			if(r.material == box.getMaterial()) r.addBox(box);
		}
		if(box.getMaterial() == Material.LUBRICANT) conveyor.addBox(box);
	}
	


	@Override
	public int getMaterials() {
		// TODO Auto-generated method stub
		return -1;
	}
	
	@Override
	public SubsystemStatus status() { //TODO
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
		}
		return status;
	}

	@Override
	public Position getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	public void start(int q) {
		int finished = 0;
		while (finished < q) {
			for(Robot r: robots) {
				r.start();
			}
			while(notReady()) { //Waiting for the robots
				//This seems like bad programming, but I think it will work
			}
			conveyor.start();
			while(conveyor.status() != SubsystemStatus.WAITING) { //Waiting for the conveyor
				
			}
			finished++;
			FactoryEvent done = new FactoryEvent(this, EventKind.CAR_FINISHED);
			this.notify(done);
		}
		FactoryEvent taskDone = new FactoryEvent(this, EventKind.TASK_FINISHED);
		this.notify(taskDone);
	}
	private boolean notReady() {
		SubsystemStatus s = SubsystemStatus.WAITING;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubsystemMenu getCurrentSubsystemMenu() {
		return null;
	}



	@Override
	public SubsystemStatus getStatus() {
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
		}
		return status;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notify(FactoryEvent event) {
		AL_Subsystem.n
	}


}