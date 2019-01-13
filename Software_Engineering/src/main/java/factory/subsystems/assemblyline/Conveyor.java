package factory.subsystems.assemblyline;

import java.awt.Graphics;

import factory.shared.Container;
import factory.shared.Position;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.assemblyline.interfaces.ConveyorMonitorInterface;
import factory.subsystems.assemblyline.interfaces.RobotInterface;

public class Conveyor implements RobotInterface, Stoppable, Placeable, ConveyorMonitorInterface{
	public int speed;
	public int lubricant;
	private SubsystemStatus status = SubsystemStatus.WAITING;
	private long timestamp;

	
	
	/**
	 * 
	 * @param s determines how many revelations (1 revelation = 4 steps) the conveyor does per minute
	 * 10 revelations are slow
	 * 20 revelations are normal
	 * 30 revelations are fast
	 * This will be considered when simulating technical failure
	 * 
	 * @param l how much lubricant is available initially
	 */
	public Conveyor(int s, int l){
		speed = (60/s) / 4; 
		lubricant = l;
	}
	
	public Conveyor(int s) { //If no lubricant level is given, it will be assumed
		speed = (60/s) / 4;
		lubricant = 100; //NOT A FINAL VALUE
	}
	
	public void addBox(Container container) {
		lubricant += container.getAmount();
	}
	

	public void doWork() {
		if(getStatus() == SubsystemStatus.WAITING) {
			//Performs task
			status = SubsystemStatus.RUNNING;
			timestamp = (int) System.currentTimeMillis();
			if(Math.random() * speed > 18) {
				status = SubsystemStatus.BROKEN;
			}
		} else {
			//THROW ERROR MESSAGE
		} 
	}

	
	public SubsystemStatus getStatus() {
		if (status == SubsystemStatus.BROKEN) { //if it's broken
			return SubsystemStatus.BROKEN;
		}
		if (timestamp + speed <= System.currentTimeMillis()) { //Done with last task
			if(lubricant > 0) { //Ready for new task
				status = SubsystemStatus.WAITING;
			} else {
				status = SubsystemStatus.STOPPED;
			}
		}
		return SubsystemStatus.RUNNING;
	}
	

	public void notifyMonitoringSystem(Task task, RobotEvent event) {
		//TODO
	}
	
	public int getMaterials() {
		return lubricant;
	}
	
	/**
	 * 
	 * @return this returns the amount of seconds it takes for one step of the conveyor
	 */
	@Override
	public double getSpeed() {
		return speed;
	}

	@Override
	public Position getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graphics getDrawable() {
		// TODO Auto-generated method stub
		return null;
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
	public void addBox(factory.subsystems.assemblyline.interfaces.Container box) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSpeed(double speed) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void addBox(factory.subsystems.assemblyline.interfaces.Container box) {
		// TODO Auto-generated method stub
		
	}
	
	

}
