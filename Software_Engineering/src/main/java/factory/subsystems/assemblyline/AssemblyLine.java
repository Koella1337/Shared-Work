package factory.subsystems.assemblyline;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import app.gui.SubsystemMenu;
import factory.shared.AbstractSubsystem;
import factory.shared.Constants;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.Task;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.assemblyline.interfaces.RobotInterface;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public class AssemblyLine extends AbstractSubsystem implements Monitorable,RobotInterface, Stoppable,  ContainerDemander, ContainerSupplier {
	public Robot[] robots;
	public Conveyor conveyor;
	private Position position;
	
	
	public AssemblyLine(MonitoringInterface monitor, Position position,Robot[] r, Conveyor c) {
		super(monitor);
		this.robots = r;
		this.conveyor = c;
		this.position = position;
		
	}
	
	public void addBox(Container box) { //Adds the box to the matching robot/conveyor
		for(Robot r: robots) {
			if(r.material == box.getMaterial()) r.addBox(box);
		}
		if(box.getMaterial() == Material.LUBRICANT) conveyor.addBox(box);
	}
	

	public void doWork() {
		for(Robot r: robots) {
			r.doWork();
		}
		if(subsysRdy()) conveyor.doWork();
	}

	
	public boolean isReady() {
		if(subsysRdy() && conveyor.isReady()) return true;
		else return false;
	}
	

	public void notifyMonitoringSystem(Task task, RobotEvent event) {
		//TODO
	}
	


	@Override
	public int getMaterials() {
		// TODO Auto-generated method stub
		return -1;
	}
	
	private boolean subsysRdy() {
		boolean ready = true;
		for(Robot r: robots) {
			if(r.isReady() != true) ready = false;
		}
		if(!conveyor.isReady()) ready = false;
		return ready;
	}


	@Override
	public void draw(Graphics g) {
		g.drawString("asssemblyline", 5, 15);
		g.drawRect(0, 0, Constants.PlaceableSize.ASSEMBLY_LINE.x,  Constants.PlaceableSize.ASSEMBLY_LINE.y);
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Container deliverContainer(Material material) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void receiveContainer(Container container) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notify(FactoryEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SubsystemStatus getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Placeable> getPlaceables() {
		List<Placeable> p = new ArrayList<>();
		//p.addAll( Arrays.asList(robots));
		p.add(this.conveyor);
		p.add(this);
		return p;
	}

	@Override
	public SubsystemMenu getCurrentSubsystemMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Position getPosition() {
		return this.position;
	}

}