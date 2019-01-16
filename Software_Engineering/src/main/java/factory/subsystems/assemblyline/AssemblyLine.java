package factory.subsystems.assemblyline;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import app.gui.SubsystemMenu;
import factory.shared.Constants;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.Utils;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.enums.MaterialStatus;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.assemblyline.interfaces.RobotInterface;

@SuppressWarnings("unused")
public class AssemblyLine implements RobotInterface, Stoppable, Placeable {
	
	private Robot[] robots = new Robot[4];
	private Conveyor conveyor;
	private Position position;
	private AL_Subsystem alsubsys;
	private int finished;
	private SubsystemStatus alstatus;
	private Material color;

	public AssemblyLine(Position pos, AL_Subsystem al, Material c) {
		position = pos.clone();
		Utils.assignSize(position, Constants.PlaceableSize.ASSEMBLY_LINE);
		color = c;
		alsubsys = al;
		alstatus = SubsystemStatus.WAITING;
		Position rpos = position.clone();
		if (Constants.DEBUG)
			System.out.println("create assemblyline at position "+position);

		// Create 4 robots with some initial materials
		robots[0] = new Robot(this, rpos, RobotTypes.GRABBER, Material.BODIES, 30); 
		rpos.xPos += (350 / 4.5);
		robots[1] = new Robot(this, rpos, RobotTypes.SCREWDRIVER, Material.SCREWS, 10);
		rpos.xPos += (350 / 4.5);
		robots[2] = new Robot(this, rpos, RobotTypes.PAINTER, color, 20);
		rpos.xPos += (350 / 4.5);
		robots[3] = new Robot(this, rpos, RobotTypes.INSPECTOR, color, 0);

		rpos = position;
		conveyor = new Conveyor(this, pos.clone(), 20, 100); // Create conveyor
	}

	public Robot[] getRobots() {
		return robots;
	}

	public void addBox(Container box) { // Adds the box to the matching robot/conveyor
		for (Robot r : robots) {
			if (r.material == box.getMaterial())
				r.addBox(box);
		}
		if (box.getMaterial() == Material.LUBRICANT)
			conveyor.addBox(box);
	}

	@Override
	public int getMaterials() {
		return -1;
	}

	@Override
	public SubsystemStatus status() {
		SubsystemStatus status = SubsystemStatus.WAITING;
		for (Robot r : robots) {
			if (r.status() == SubsystemStatus.RUNNING && status == SubsystemStatus.WAITING) {
				status = r.status();
			}
			if (r.status() == SubsystemStatus.STOPPED && status != SubsystemStatus.BROKEN) {
				status = r.status();
			}
			if (r.status() == SubsystemStatus.BROKEN) {
				status = r.status();
			}
			System.out.println(r.status());
		}
		if (conveyor.status() == SubsystemStatus.RUNNING && status == SubsystemStatus.WAITING) {
			status = conveyor.status();
		}
		if (conveyor.status() == SubsystemStatus.STOPPED && status != SubsystemStatus.BROKEN) {
			status = conveyor.status();
		}
		if (conveyor.status() == SubsystemStatus.BROKEN) {
			status = conveyor.status();
		}

		return status;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void draw(Graphics g) {
		// there is no need to add the robots etc. here
		g.drawRect(0, 0, position.xSize, position.ySize);
	}
	
	@Override
	public void start() {
		alstatus = SubsystemStatus.RUNNING;
		start(500);
	}

	public void start(int q) {
		double speed = q / 10; // Adaptive speed
		if (speed > 30)
			speed = 30;
		else if (speed < 10)
			speed = 10; // Boundaries for the speed
		conveyor.setSpeed(speed);
		
		try {
			Random rng = new Random();
			int sim_mintime = 1000;
			int sim_bound = 6000;
			
			while (alstatus == SubsystemStatus.RUNNING) {
				for (Robot r : robots) {
					r.start();
				}
				Thread.sleep(rng.nextInt(sim_bound) + sim_mintime);
				while (notReady()) { // Waiting for the robots
					Thread.sleep(rng.nextInt(sim_bound) + sim_mintime);
				}
				while (conveyor.status() != SubsystemStatus.WAITING) { // Waiting for the conveyor
					Thread.sleep(rng.nextInt(sim_bound) + sim_mintime);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}

	private boolean notReady() {
		for (Robot r : robots) {
			if (r.status() != SubsystemStatus.WAITING) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void stop() {
		for (Robot r : robots) {
			r.stop();
		}
		conveyor.stop();
		alstatus = SubsystemStatus.STOPPED;
	}

	public List<Placeable> getPlaceables() {
		List<Placeable> plc = new ArrayList<Placeable>();
		for (Robot r : robots) {
			plc.add(r);
		}
		plc.add(conveyor);
		plc.addAll(conveyor.getPlaceables());
		plc.add(this);
		return plc;
	}

	public void stoppedSys(Object source, FactoryEvent event) {
		this.notify(new FactoryEvent(alsubsys, EventKind.CAR_FINISHED));
	}

	public SubsystemStatus getStatus() { // This should only be used for test cases
		SubsystemStatus alstatus = SubsystemStatus.WAITING;
		for (Robot r : robots) {
			if (r.status() == SubsystemStatus.RUNNING && alstatus == SubsystemStatus.WAITING) {
				alstatus = r.status();
			}
			if (r.status() == SubsystemStatus.STOPPED && alstatus != SubsystemStatus.BROKEN) {
				alstatus = r.status();
			}
			if (r.status() == SubsystemStatus.BROKEN) {
				alstatus = r.status();
			}
		}
		return alstatus;
	}

	public void notify(FactoryEvent event) {
		if (event.getKind() == EventKind.CAR_FINISHED) {
			finished++;
			Material car;
			switch (color) {
			case COLOR_BLACK:
				car = Material.CAR_BLACK;
				break;
			case COLOR_BLUE:
				car = Material.CAR_BLUE;
				break;
			case COLOR_GRAY:
				car = Material.CAR_GRAY;
				break;
			case COLOR_GREEN:
				car = Material.CAR_GREEN;
				break;
			case COLOR_RED:
				car = Material.CAR_RED;
				break;
			case COLOR_WHITE:
				car = Material.CAR_WHITE;
				break;
			default:
				car = Material.CAR_BLACK;
				break;
			}
			conveyor.getOutputbox().receiveContainer(new Container(car));
			if (conveyor.getOutputbox().getFullness() == MaterialStatus.BAD) {
				FactoryEvent full = new FactoryEvent(getALSys(), EventKind.RESOURCEBOX_ALMOST_FULL, car,
						conveyor.getOutputbox());
				alsubsys.notify(full);
			}
		}
		alsubsys.notify(event);
	}

	public AL_Subsystem getALSys() {
		return alsubsys;
	}

	public void restart() {
		for (Robot r : robots) {
			r.restart();
		}
		conveyor.restart();
		start();
	}

	public Conveyor getConveyor() {
		return conveyor;
	}

	public Material getMaterial() {
		return color;
	}

	public List<Placeable> getAGVRobot() {
		List<Placeable> plc = new ArrayList<Placeable>();
		for (Robot r : robots) {
			if (r.robot == RobotTypes.PAINTER || r.robot == RobotTypes.SCREWDRIVER) {
				plc.add(r);
			}
		}
		return plc;
	}

	public List<Placeable> getAGVConveyor() {
		List<Placeable> plc = new ArrayList<Placeable>();
		plc.add(conveyor);
		return plc;
	}

	public List<Placeable> getAGVOutputbox() {
		List<Placeable> plc = new ArrayList<Placeable>();
		plc.add(conveyor.getOutputbox());
		return plc;
	}

}