package factory.subsystems.assemblyline;

import java.awt.Graphics;
import java.util.List;

import app.gui.SubsystemMenu;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;

public class AssemblyLine implements Monitorable, Stoppable, Placeable, ContainerDemander, ContainerSupplier {
	public Robot[] robots;
	public Conveyor conveyor;

	public AssemblyLine(Robot[] r, Conveyor c) {
		robots = r;
		conveyor = c;
	}

	public void addBox(Container box) { // Adds the box to the matching robot/conveyor
		for (Robot r : robots) {
			if (r.material == box.getMaterial())
				r.addBox(box);
		}
		if (box.getMaterial() == Material.LUBRICANT)
			conveyor.addBox(box);
	}

	public void doWork() {
		for (Robot r : robots) {
			r.doWork();
		}
		if (subsysRdy())
			conveyor.doWork();
	}

	public boolean isReady() {
		if (subsysRdy() && conveyor.isReady())
			return true;
		else
			return false;
	}

	private boolean subsysRdy() {
		boolean ready = true;
		for (Robot r : robots) {
			if (r.isReady() != true)
				ready = false;
		}
		if (!conveyor.isReady())
			ready = false;
		return ready;
	}

	@Override
	public Position getPosition() {
		return new Position(50,50);
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub

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
	public void receiveContainer(Container container) {
		// TODO Auto-generated method stub

	}

	@Override
	public Container deliverContainer(Material material) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubsystemMenu getCurrentSubsystemMenu() {
		// TODO Auto-generated method stub
		return null;
	}

}
