package factory.subsystems.assemblyline;

import java.awt.Color;
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
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.assemblyline.interfaces.ConveyorMonitorInterface;
import factory.subsystems.assemblyline.interfaces.RobotInterface;

@SuppressWarnings("unused")
public class Conveyor implements Monitorable, RobotInterface, Stoppable, Placeable, ConveyorMonitorInterface {
	public double speed;
	public int lubricant;
	private SubsystemStatus status = SubsystemStatus.WAITING;
	private long timestamp;
	private ResourceBox inputbox;
	private ResourceBox outputbox;
	private Container box;
	private AssemblyLine al;
	private Position position;

	/**
	 * 
	 * @param s
	 *            determines how many revelations (1 revelation = 4 steps) the
	 *            conveyor does per minute 10 revelations are slow 20 revelations
	 *            are normal 30 revelations are fast This will be considered when
	 *            simulating technical failure
	 * 
	 * @param l
	 *            how much lubricant is available initially
	 */
	public Conveyor(AssemblyLine al, Position pos, int s, int l) {
		speed = (60 / s) / 4;
		lubricant = l;
		this.al = al;
		position = pos.clone();
		position.yPos += 60;
		position.xSize = 310;
		position.ySize = 40;
		outputbox = new ResourceBox(al.getALSys(), new Position(position.xPos+position.xSize, position.yPos));
		box = new Container(al.getMaterial());
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
	 * 
	 * @return this returns the amount of seconds it takes for one step of the
	 *         conveyor
	 */
	@Override
	public double getSpeed() {
		return speed;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void start() {
		if (status() == SubsystemStatus.WAITING) {
			lubricant -= Math.random();
			if (lubricant < 10) {
				FactoryEvent event = new FactoryEvent(al.getALSys(), EventKind.CONVEYORS_LACK_OF_OIL, this);
				notify(event);
			}
			// Performs task
			status = SubsystemStatus.RUNNING;
			timestamp = (int) System.currentTimeMillis();
			if (Math.random() * speed > 25 * lubricant / 100) { // Simulation on how speed & lubricant impact chances of
																// breaking
				status = SubsystemStatus.BROKEN;
				FactoryEvent broken = new FactoryEvent(al.getALSys(), EventKind.CONVEYORS_BROKEN, this);
				notify(broken);
			}
		}
	}

	@Override
	public void stop() {
		status = SubsystemStatus.STOPPED;
	}

	@Override
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(1, 1, position.xSize - 1, position.ySize - 1);
		g.setColor(Constants.UI_BORDER_COLOR);
		g.drawRect(0, 0, position.xSize, position.ySize);
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
		String s = "Conveyor @ " + position.xPos + " / " + position.yPos;
		return s;
	}

	@Override
	public SubsystemStatus getStatus() {
		return status();
	}

	@Override
	public List<Placeable> getPlaceables() {
		List<Placeable> l = new ArrayList<Placeable>();
		l.add(this);
		l.add(outputbox);
		return l;
	}

	@Override
	public SubsystemMenu getCurrentSubsystemMenu() {
		return null;
	}

	@Override
	public void notify(FactoryEvent event) {
		al.notify(event);
	}

	public void restart() {
		status = SubsystemStatus.WAITING;
	}

}
