package factory.subsystems.assemblyline;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import app.gui.SubsystemMenu;
import factory.shared.Constants;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.Monitorable;
import factory.shared.interfaces.Placeable;
import factory.subsystems.assemblyline.interfaces.RobotInterface;

public class Robot implements Monitorable, RobotInterface, ContainerDemander {
	public RobotTypes robot;
	public Material material;
	public int materials;
	public final Position position;
	private SubsystemStatus status = SubsystemStatus.WAITING;
	private long timestamp = 0;
	private AssemblyLine al;
	private Material car;
	
	public Robot(AssemblyLine al, Position pos, RobotTypes r, Material mat, int mats) {
		robot = r;
		materials = mats;
		position = pos.clone();
		this.al = al;
		position.xSize = 50;
		position.ySize = 50;
		material = mat;
	}
	
	@Override
	public void receiveContainer(Container container) {
		materials += Objects.requireNonNull(container).getAmount();
		System.out.println(materials);
	}
	
	public void addBox(Container container) {
		materials += container.getAmount();
	}
	
	public SubsystemStatus status() {
		if (status == SubsystemStatus.STOPPED || status == SubsystemStatus.BROKEN) {
			if(status== SubsystemStatus.BROKEN) {
				FactoryEvent broken = new FactoryEvent(al.getALSys(), EventKind.ROBOTARMS_BROKEN, this);
				this.notify(broken);
			}
			return status;
		}
		
		if(timestamp+5000 <= System.currentTimeMillis()) { //Finished with task
			status = SubsystemStatus.WAITING;
			return SubsystemStatus.WAITING;
		} else {
			status = SubsystemStatus.RUNNING;
			return SubsystemStatus.RUNNING;
		}
	}
	
	public int getMaterials() {
		return materials;
	}

	@Override
	public Position getPosition() {
		return position;
	}
	
	private long lastLackOfMaterialSent = 0;
	
	@Override
	public void start() {
		if(status() == SubsystemStatus.WAITING) {
			if(robot == RobotTypes.SCREWDRIVER || robot ==  RobotTypes.PAINTER) {
				
				materials -= 5;
				
				if(materials < 10 && (System.currentTimeMillis() - lastLackOfMaterialSent) > 50000) {
					lastLackOfMaterialSent = System.currentTimeMillis();
					FactoryEvent lowmat = new FactoryEvent(al.getALSys(), EventKind.ROBOTARMS_LACK_OF_MATERIAL, material, this);
					this.notify(lowmat);
				}
			} else if(robot == RobotTypes.INSPECTOR) {
				if(Math.random() < 0.95) {
					if(material != null)
					{
						switch(material) {
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
					}
					else
					{
						car = Material.CAR_BLACK;
					}
					FactoryEvent done = new FactoryEvent(al.getALSys(), EventKind.CAR_FINISHED, car, al.getConveyor().getOutputbox());
					notify(done);
				}
			}
			
			timestamp = System.currentTimeMillis(); //The Robot takes 5 seconds to perform it's task
		}
		status();
	}

	@Override
	public void stop() {
		status = SubsystemStatus.STOPPED;
	}
	
	@Override
	public void draw(Graphics g) {
		switch(status) {
		case RUNNING:
			if(robot == RobotTypes.PAINTER) {
				switch(material) {
				case COLOR_BLACK:
					g.setColor(Color.BLACK);
					break;
				case COLOR_BLUE:
					g.setColor(Color.BLUE);
					break;
				case COLOR_GRAY:
					g.setColor(Color.GRAY);
					break;
				case COLOR_GREEN:
					g.setColor(Color.GREEN);
					break;
				case COLOR_RED:
					g.setColor(Color.RED);
					break;
				case COLOR_WHITE:
					g.setColor(Color.WHITE);
					break;
				default:
					g.setColor(Color.LIGHT_GRAY);
					break;
				}
			} else	
				g.setColor(Color.LIGHT_GRAY);
			break;
		default:
			g.setColor(Color.LIGHT_GRAY);
			break;
		}
		g.fillRect(1, 1, position.xSize - 1, position.ySize - 1);
		g.setColor(Constants.UI_BORDER_COLOR);
		g.drawRect(0, 0, position.xSize, position.ySize);
	}

	@Override
	public String getName() {
		String s = "Robot Type " + robot + " @ " + position.xPos + " / " + position.yPos;
		return s;
	}

	@Override
	public void notify(FactoryEvent event) {
		al.notify(event);
	}

	@Override
	public SubsystemStatus getStatus() {
		return status();
	}

	@Override
	public List<Placeable> getPlaceables() {
		List<Placeable> l = new ArrayList<Placeable>();
		l.add(this);
		return l;
	}

	@Override
	public SubsystemMenu getCurrentSubsystemMenu() {
		return null;
	}
	
	public void restart() {
		status = SubsystemStatus.WAITING;
	}



}
