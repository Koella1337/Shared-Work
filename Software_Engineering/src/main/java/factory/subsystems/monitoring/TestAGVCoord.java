package factory.subsystems.monitoring;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.enums.EventKind;
import factory.shared.interfaces.Placeable;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.agv.AgvTask;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

public class TestAGVCoord extends AgvCoordinator {

	private TestForklift forklift;
	List<Placeable> plac;
	
	public TestAGVCoord(MonitoringInterface mon) {
		super(mon, null, null);
		this.forklift = new TestForklift();
		this.forklift.pos = new Position(100,100);
		this.plac = new ArrayList<>();
		this.plac.add(this.forklift);
	}

	@Override
	public void receiveTask(AgvTask task) {
		System.out.println("TestAGVCoord SUBMIT AGV TASK");
		Container container = task.getPickup().deliverContainer(task.getMaterial());
		
		Position start = task.getPickup().getPosition();
		
		Position end = task.getDropoff().getPosition();
		
		Position diff = Position.subtractPosition(end, start);
		
		this.forklift.pos = start;
		new Thread( () ->{ 
		
		while(Position.length(Position.subtractPosition(end, this.forklift.pos)) > 10) {
			 
			
			this.forklift.pos = Position.addPosition(this.forklift.pos, Position.divide(diff, 30));
			System.out.println(this.forklift.pos);
			
			
			try {
				Thread.sleep(100l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		}).start();
		
		task.getDropoff().receiveContainer(container);
		this.notify(new FactoryEvent(this, EventKind.AGV_CONTAINER_DELIVERED, task));
	}

	@Override
	public List<Placeable> getPlaceables() {
		return plac;
	}

	private class TestForklift implements Placeable {

		public Position pos;
		
		@Override
		public Position getPosition() {
			return pos;
		}

		@Override
		public void draw(Graphics g) {
			g.drawString("f",0, 0);
		}

	}
}
