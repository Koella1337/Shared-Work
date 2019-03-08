package factory.subsystems.assemblyline;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.w3c.dom.Element;

import factory.shared.Constants;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.Utils;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.enums.MaterialStatus;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Placeable;
import factory.subsystems.assemblyline.interfaces.RobotInterface;
import factory.subsystems.warehouse.AssemblyLineDirection;

public class AssemblyLine implements Placeable {
	
	private final int id;
	private final AssemblyLineSystem subsystem;
	
	private final RobotInterface robotGrabber;
	private final RobotInterface robotScrewDriver;
	private final RobotInterface robotPainter;
	private final RobotInterface robotInspector;
	
	private final List<Placeable> placeables = new ArrayList<>();
	private final List<RobotInterface> robots = new ArrayList<>();
	private final Conveyor conveyor;
	private final ResourceBox outputBox;
	
	private final Position position;
	private final AssemblyLineDirection direction;
	private final Material color;
	
	public AssemblyLine(int id, AssemblyLineSystem subsystem, Material color, Element xmlAssemblyLineElem) {
		this.id = id;
		
		//general init
		this.color = color;
		this.subsystem = subsystem;
		
		this.position = Utils.xmlGetPositionFromElement(xmlAssemblyLineElem);
		Utils.assignSize(position, Constants.PlaceableSize.ASSEMBLY_LINE);
		this.direction = determineDirection(xmlAssemblyLineElem);
		
		if (Constants.DEBUG)
			System.out.println("Building AssemblyLine at " + position);

		//set up assembly line "building"
		Position robotPos = position.clone();
		Utils.assignSize(robotPos, Constants.PlaceableSize.ROBOT);
		
		int robotOffset = (int) (Constants.PlaceableSize.ASSEMBLY_LINE.x / 4.5);
		if (direction == AssemblyLineDirection.MINUS_X) {
			robotOffset *= -1;
			robotPos.xPos += (position.xSize - robotPos.xSize);
		}
		
		Position conveyorPos = position.clone();
		Utils.assignSize(conveyorPos, Constants.PlaceableSize.CONVEYOR);
		
		Position outputboxPos = position.clone();
		Utils.assignSize(outputboxPos, Constants.PlaceableSize.RESOURCE_BOX);
		
		//build robots
		robotGrabber = new Robot(this, robotPos, RobotType.GRABBER, Material.BODIES, 12);
		robots.add(robotGrabber);
		if (Constants.DEBUG) System.out.println("--> Built " + robotGrabber);
		
		robotPos = robotPos.clone();
		robotPos.xPos += robotOffset;
		robotScrewDriver = new Robot(this, robotPos, RobotType.SCREWDRIVER, Material.SCREWS, 55);
		robots.add(robotScrewDriver);
		if (Constants.DEBUG) System.out.println("--> Built " + robotScrewDriver);
		
		robotPos = robotPos.clone();
		robotPos.xPos += robotOffset;
		robotPainter = new Robot(this, robotPos, RobotType.PAINTER, color, 23);
		robots.add(robotPainter);
		if (Constants.DEBUG) System.out.println("--> Built " + robotPainter);
		
		robotPos = robotPos.clone();
		robotPos.xPos += robotOffset;
		robotInspector = new Robot(this, robotPos, RobotType.INSPECTOR, color, 0);
		robots.add(robotInspector);
		if (Constants.DEBUG) System.out.println("--> Built " + robotInspector);
		
		//build conveyor
		conveyorPos.yPos += (position.ySize - conveyorPos.ySize);
		if (direction == AssemblyLineDirection.MINUS_X)
			conveyorPos.xPos += outputboxPos.xSize;
		conveyor = new Conveyor(this, conveyorPos, 100);
		if (Constants.DEBUG) System.out.println("--> Built " + conveyor);
		
		//build outputbox
		outputboxPos.yPos += (position.ySize - outputboxPos.ySize);
		if (direction == AssemblyLineDirection.PLUS_X)
			outputboxPos.xPos += conveyorPos.xSize;
		outputBox = new ResourceBox(subsystem, outputboxPos);
		if (Constants.DEBUG) System.out.println("--> Built " + outputBox);
		
		//setup placeables list
		placeables.addAll(robots);
		placeables.add(conveyor);
		placeables.add(outputBox);
	}
	
	private AssemblyLineDirection determineDirection(Element xmlAssemblyLineElem) {
		String direction = xmlAssemblyLineElem.getElementsByTagName("direction").item(0).getTextContent();
		switch (direction.trim()) {
			case "+x": return AssemblyLineDirection.PLUS_X;
			case "-x": return AssemblyLineDirection.MINUS_X;
			default:
				return AssemblyLineDirection.PLUS_X;	//default in case of invalid xml-textcontent
		}
	}
	
	protected AssemblyLineSystem getSubsystem() {
		return subsystem;
	}

	public int getId() {
		return id;
	}

	@Override
	public Position getPosition() {
		return position;
	}
	
	public AssemblyLineDirection getDirection() {
		return direction;
	}
	
	public ResourceBox getOutputBox() {
		return outputBox;
	}

	@Override
	public void draw(Graphics g) {
		g.drawRect(0, 0, position.xSize, position.ySize);
	}
	
	protected void start() {
		try {
			Random rng = new Random();
			int sim_mintime = 1000;
			int sim_bound = 6000;
			
			while (true) {
				if (subsystem.getStatus() != SubsystemStatus.RUNNING) {
					Thread.sleep(1000);
					continue;
				}
				
				while (!areRobotsReady()) { // Waiting for the robots to be ready
					Thread.sleep(rng.nextInt(sim_bound) + sim_mintime);
				}
				
				for (RobotInterface robot : robots) {
					robot.start();
				}
				Thread.sleep(rng.nextInt(sim_bound) + sim_mintime);
				
				while (!conveyor.isReady()) { // Waiting for the conveyor to be ready
					Thread.sleep(rng.nextInt(sim_bound) + sim_mintime);
				}
				conveyor.start();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean areRobotsReady() {
		for (RobotInterface robot : robots) {
			if (!robot.isReady()) {
				return false;
			}
		}
		return true;
	}

	public void produceDifferentColoredCars(Material color, int quantity) {
		//TODO: implement
	}

	public List<Placeable> getPlaceables() {
		return placeables;
	}

	public void notifySubsystem(FactoryEvent event) {
		if (event.getKind() == EventKind.CAR_FINISHED) {
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
			outputBox.receiveContainer(new Container(car));
			
			MaterialStatus fullness = outputBox.getFullness();
			if (fullness == MaterialStatus.BAD || fullness == MaterialStatus.TERRIBLE) {
				FactoryEvent full = new FactoryEvent(subsystem, EventKind.RESOURCEBOX_ALMOST_FULL, outputBox);
				subsystem.notify(full);
			}
		}
		subsystem.notify(event);
	}

	public Conveyor getConveyor() {
		return conveyor;
	}

	public Material getColor() {
		return color;
	}

}