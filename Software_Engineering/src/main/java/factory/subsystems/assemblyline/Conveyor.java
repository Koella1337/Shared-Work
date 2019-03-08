package factory.subsystems.assemblyline;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Objects;
import java.util.Random;

import factory.shared.Constants;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.enums.MaterialStatus;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.subsystems.assemblyline.interfaces.ConveyorInterface;
import factory.subsystems.warehouse.AssemblyLineDirection;

class Conveyor implements ConveyorInterface, ContainerDemander {
	
	/** Time in ms how long the conveyor takes to perform work. */
	private static final long SIMULATED_WORK_DURATION = 3000;
	
	/** How much lubricant is used (at maximum) when the Conveyor is started once. */
	private static final int MAXIMUM_LUBRICANT_PER_START = 10;
	
	/**
	 * Amount of seconds for 1 conveyor step
	 */
	private double speed = 1;
	
	private Random lubricantRandomizer = new Random();
	private int lubricantAmount;
	
	/** Timestamp of when the last {@link EventKind#LACK_OF_MATERIAL} event was sent. */
	private long lastLackOfLubricantEventSent = 0;
	
	private ComponentStatus status = ComponentStatus.READY;
	
	private AssemblyLine assemblyLine;
	private Position position;

	/**
	 * @param assemblyLine
	 * 			the AssemblyLine that owns this conveyor-belt
	 * @param initialLubricant
	 *          how much lubricant is available initially
	 */
	public Conveyor(AssemblyLine assemblyLine, Position position, int initialLubricant) {
		this.lubricantAmount = initialLubricant;
		this.assemblyLine = assemblyLine;
		this.position = position;
	}

	@Override
	public void receiveContainer(Container container) {
		lubricantAmount += Objects.requireNonNull(container).getAmount();
		if (status == ComponentStatus.OUT_OF_MATERIAL) {
			status = ComponentStatus.READY;
		}
	}

	public int getMaterials() {
		return lubricantAmount;
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
	
	@Override
	public boolean isReady() {
		return status == ComponentStatus.READY;
	}
	
	@Override
	public void start() {
		if (!isReady())
			throw new IllegalArgumentException("Robot started even though it was not ready!");
		
		status = ComponentStatus.WORKING;
		
		lubricantAmount -= lubricantRandomizer.nextInt(MAXIMUM_LUBRICANT_PER_START + 1);
		if (lubricantAmount < (MAXIMUM_LUBRICANT_PER_START * 5) && (System.currentTimeMillis() - lastLackOfLubricantEventSent) > 50000) {
			lastLackOfLubricantEventSent = System.currentTimeMillis();
			FactoryEvent lackOfLubricantEvent = new FactoryEvent(
				assemblyLine.getSubsystem(), 
				EventKind.LACK_OF_MATERIAL, 
				Material.LUBRICANT, this
			);
			assemblyLine.notifySubsystem(lackOfLubricantEvent);
		}
		
		// Performs task
		if (Math.random() * speed > 25 * lubricantAmount / 100) { // Simulation on how speed & lubricant impact chances of breaking
			FactoryEvent broken = new FactoryEvent(assemblyLine.getSubsystem(), EventKind.CONVEYOR_BROKEN, this);
			assemblyLine.notifySubsystem(broken);
		}
		try {
			Thread.sleep(SIMULATED_WORK_DURATION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (lubricantAmount >= MAXIMUM_LUBRICANT_PER_START) {
			status = ComponentStatus.READY;
		} else {
			status = ComponentStatus.OUT_OF_MATERIAL;
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
		
		//TODO: maybe change colors on other status
		if (assemblyLine.getSubsystem().getStatus() == SubsystemStatus.RUNNING)
			g.setColor(MaterialStatus.PERFECT.uiColor);
		else
			g.setColor(MaterialStatus.TERRIBLE.uiColor);
		
		Font prevFont = g.getFont();
		g.setFont(new Font("TimesNewRoman", Font.BOLD, 32));
		g.drawString(directionString, 5, (int) (position.ySize / 1.6f));
		
		g.setFont(prevFont);
		g.setColor(Color.WHITE);
		g.drawString(""+lubricantAmount, (position.xSize / 2) - 20, position.ySize - 5);
	}
	
	@Override
	public String toString() {
		return "(Conveyor at " + position.toString() + ")";
	}
	
}
