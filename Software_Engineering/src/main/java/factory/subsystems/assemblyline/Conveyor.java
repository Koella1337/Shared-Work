package factory.subsystems.assemblyline;

import java.awt.Graphics;
import java.util.List;

import app.gui.SubsystemMenu;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.Task;
import factory.shared.enums.EventKind;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.assemblyline.interfaces.ConveyorMonitorInterface;
import factory.subsystems.assemblyline.interfaces.RobotInterface;

public class Conveyor implements Monitorable, RobotInterface, Stoppable, Placeable, ConveyorMonitorInterface{
	public int speed;
	public int lubricant;
	private SubsystemStatus status = SubsystemStatus.WAITING;
	private long timestamp;
	private ResourceBox inputbox;
	private ResourceBox outputbox;

	
	
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
	

	
	public SubsystemStatus status() {
		if (status == SubsystemStatus.BROKEN) { //if it's broken
			FactoryEvent broken = new FactoryEvent(this, EventKind.CONVEYORS_BROKEN);
			this.notify(broken);
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
	public void start() {
		if(status == SubsystemStatus.WAITING) {
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

	@Override
	public void stop() {
		status = SubsystemStatus.STOPPED;
	}

	@Override
	public void setSpeed(double speed) {
		this.speed = (int) speed;
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}


	public ResourceBox getInputbox() {
		return inputbox;
	}

	public void setInputbox(ResourceBox inputbox) {
		this.inputbox = inputbox;
	}

	public ResourceBox getOutputbox() {
		return outputbox;
	}

	public void setOutputbox(ResourceBox outputbox) {
		this.outputbox = outputbox;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
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
