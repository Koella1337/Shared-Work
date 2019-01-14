package factory.subsystems.assemblyline;
import java.awt.Graphics;
import java.util.List;

import app.gui.SubsystemMenu;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.subsystems.assemblyline.interfaces.RobotInterface;

public class Robot implements Monitorable, RobotInterface,  ContainerDemander{
	public RobotTypes robot;
	public Material material;
	public int materials;
	public Position position;
	private SubsystemStatus status = SubsystemStatus.WAITING;
	private int timestamp = 0;
	private AssemblyLine al;
	
	
	public Robot(RobotTypes r, int mats, Position pos, AssemblyLine al) {
		robot = r;
		materials = mats;
		position = pos;
		this.al = al;
		position.xSize = 10;
		position.ySize = 10;
	}
	
	public void addBox(Container container) {
		material = container.getMaterial();
		materials += container.getAmount();
	}
	

	
	public SubsystemStatus status() {
		if (status == SubsystemStatus.STOPPED || status == SubsystemStatus.BROKEN) {
			if(status== SubsystemStatus.BROKEN) {
				FactoryEvent broken = new FactoryEvent(al.getALSys(), EventKind.ROBOTARMS_BROKEN, this);
				this.notify(broken);
			}
			return status;
		}
		
		if(timestamp+5 <= System.currentTimeMillis()) { //Finished with task
			status = SubsystemStatus.WAITING;
			return SubsystemStatus.WAITING;
		} else {
			status = SubsystemStatus.RUNNING;
			return SubsystemStatus.RUNNING;
		}
	}
	
	public int getMaterials() {
		return materials;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void start() {
		if(status == SubsystemStatus.WAITING) {
			
			if(robot == RobotTypes.SCREWDRIVER || robot ==  RobotTypes.PAINTER) {
				materials--;
				if(materials < 10) {
					FactoryEvent lowmat = new FactoryEvent(al.getALSys(), EventKind.ROBOTARMS_LACK_OF_MATERIAL, this);
					this.notify(lowmat);
				}	
			} else if(robot == RobotTypes.INSPECTOR) {
				if(Math.random() < 0.95) {
					FactoryEvent done = new FactoryEvent(al.getALSys(), EventKind.CAR_FINISHED,Material.CAR, this);
					this.notify(done);
				}
			}
			
			timestamp = (int) System.currentTimeMillis(); //The Robot takes 5 seconds to perform it's task
		}
		
	}

	@Override
	public void stop() {
		status = SubsystemStatus.STOPPED;
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void receiveContainer(Container container) {
		material = container.getMaterial();
		materials += container.getAmount();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notify(FactoryEvent event) {
		al.notify(event);
	}

	@Override
	public SubsystemStatus getStatus() {
		return status();
	}

	@Override
	public List<Placeable> getPlaceables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubsystemMenu getCurrentSubsystemMenu() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void fixBroken() {
		if(status() == SubsystemStatus.BROKEN) {
			status = SubsystemStatus.WAITING;
		}
	}


}
