package factory.subsystems.assemblyline;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import app.gui.SubsystemMenu;
import factory.shared.Constants;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.enums.EventKind;
import factory.shared.enums.MaterialStatus;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.assemblyline.interfaces.ConveyorInterface;
import factory.subsystems.assemblyline.interfaces.RobotInterface;
import factory.subsystems.warehouse.AssemblyLineDirection;

@SuppressWarnings("unused")
public class Conveyor implements ConveyorInterface, ContainerDemander {
	
	/**
	 * Amount of seconds for 1 conveyor step
	 */
	private double speed = 1;
	
	public int lubricant;
	private SubsystemStatus status = SubsystemStatus.WAITING;
	private long timestamp;
	
	private ResourceBox inputbox;
	private ResourceBox outputbox;
	
	private AssemblyLine assemblyLine;
	private Position position;

	/**
	 * @param assemblyLine
	 * 			the AssemblyLine that owns this conveyor-belt
	 * @param initialLubricant
	 *          how much lubricant is available initially
	 */
	public Conveyor(AssemblyLine assemblyLine, Position position, int initialLubricant) {
		this.lubricant = initialLubricant;
		this.assemblyLine = assemblyLine;
		this.position = position;
	}

	@Override
	public void receiveContainer(Container container) {
		this.addBox(container);
	}
	
	public void addBox(Container container) {
		lubricant += container.getAmount();
	}

	public SubsystemStatus status() {
		if (status == SubsystemStatus.BROKEN) { // if it's broken
			return SubsystemStatus.BROKEN;
		} else if (timestamp + speed <= System.currentTimeMillis()) { // Done with last task
			status = SubsystemStatus.WAITING;
		} else {
			return SubsystemStatus.RUNNING;
		}
		return status;
	}

	public int getMaterials() {
		return lubricant;
	}

	/**
	 * @return this returns the amount of seconds it takes for one step of the conveyor
	 */
	@Override
	public double getSpeed() {
		return speed;
	}
	
	@Override
	public void setSpeed(double speed) {
		this.speed = speed;
	}


	@Override
	public Position getPosition() {
		return position;
	}

	private long lastLackOfMaterialSent = 0;
	
	@Override
	public void start() {
		if (status() == SubsystemStatus.WAITING) {
			lubricant -= Math.random() * 5;
			if (lubricant < 10 && (System.currentTimeMillis() - lastLackOfMaterialSent) > 50000) {
				lastLackOfMaterialSent = System.currentTimeMillis();
				FactoryEvent event = new FactoryEvent(assemblyLine.getSubsystem(), EventKind.CONVEYORS_LACK_OF_OIL, this);
				assemblyLine.notifySubsystem(event);
			}
			// Performs task
			status = SubsystemStatus.RUNNING;
			timestamp = (int) System.currentTimeMillis();
			if (Math.random() * speed > 25 * lubricant / 100) { // Simulation on how speed & lubricant impact chances of breaking
				status = SubsystemStatus.BROKEN;
				FactoryEvent broken = new FactoryEvent(assemblyLine.getSubsystem(), EventKind.CONVEYORS_BROKEN, this);
				assemblyLine.notifySubsystem(broken);
			}
		}
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(1, 1, position.xSize - 1, position.ySize - 1);
		g.setColor(Constants.UI_BORDER_COLOR);
		g.drawRect(0, 0, position.xSize, position.ySize);
		
		String directionString;
		if (assemblyLine.getDirection() == AssemblyLineDirection.MINUS_X) 
			directionString = "< < < < < < < < < < <";
		else
			directionString = "> > > > > > > > > > >";
		
		//TODO: maybe change colors on other subsystem/assemblyline stati
		if (assemblyLine.getSubsystem().getStatus() == SubsystemStatus.RUNNING)
			g.setColor(MaterialStatus.PERFECT.uiColor);
		else
			g.setColor(MaterialStatus.TERRIBLE.uiColor);
		
		Font prevFont = g.getFont();
		g.setFont(new Font("TimesNewRoman", Font.BOLD, 32));
		g.drawString(directionString, 5, (int) (position.ySize / 1.3f));
		g.setFont(prevFont);
	}

	public ResourceBox getInputbox() {
		return inputbox;
	}

	public void setInputbox(ResourceBox inputbox) {
		this.inputbox = inputbox;
	}

	public void restart() {
		status = SubsystemStatus.WAITING;
	}
	
	@Override
	public String toString() {
		return "(Conveyor at " + position.toString() + ")";
	}
}
