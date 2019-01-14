package factory.subsystems.assemblyline;
import java.awt.Graphics;

import factory.shared.Container;
import factory.shared.Position;
import factory.shared.Task;
import factory.shared.enums.Material;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.assemblyline.interfaces.RobotInterface;

public class Robot implements RobotInterface, Stoppable, Placeable{
	public RobotTypes robot;
	public Material material;
	public int materials;
	public int id;
	public Position position;
	public boolean ready;
	private int timestamp = 0;
	
	
	public Robot(RobotTypes r, int mats, int id,Position pos) {
		robot = r;
		materials = mats;
		id = this.id;
		position = pos;
	}
	
	public void addBox(Container container) {
		material = container.getMaterial();
		materials += container.getAmount();
	}
	

	public void doWork() {
		//Performs task
		ready = false;
		materials--;
		timestamp = (int) System.currentTimeMillis(); //The Robot takes 5 seconds to perform it's task
		
	}

	
	public boolean isReady() {
		if (timestamp == 0 && materials > 0) {
			return true;
		} else {
			if(timestamp+5 <= System.currentTimeMillis() && materials > 0) {
				return true;
			} else return false;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics g) {
		g.drawString("robot", 0, 0);
		
	}

	@Override
	public void notifyMonitoringSystem(Task task, RobotEvent event) {
		// TODO Auto-generated method stub
		
	}

}
