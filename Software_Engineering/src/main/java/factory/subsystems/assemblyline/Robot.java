package factory.subsystems.assemblyline;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Objects;

import factory.shared.Constants;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.subsystems.assemblyline.interfaces.RobotInterface;

public class Robot implements RobotInterface, ContainerDemander {
	
	public RobotType type;
	
	public Material materialType;
	public int materialAmount;
	
	public final Position position;
	private SubsystemStatus status = SubsystemStatus.WAITING;
	private long timestamp = 0;
	private AssemblyLine assemblyLine;
	private Material car;
	
	public Robot(AssemblyLine assemblyLine, Position pos, RobotType type, Material materialType, int initialMaterialAmount) {
		this.assemblyLine = assemblyLine;
		this.type = type;
		this.materialType = materialType;
		this.materialAmount = initialMaterialAmount;
		this.position = pos;
	}
	
	@Override
	public void receiveContainer(Container container) {
		materialAmount += Objects.requireNonNull(container).getAmount();
	}
	
	public void addBox(Container container) {
		materialAmount += container.getAmount();
	}
	
	public SubsystemStatus status() {
		if (status == SubsystemStatus.STOPPED || status == SubsystemStatus.BROKEN) {
			if(status== SubsystemStatus.BROKEN) {
				FactoryEvent broken = new FactoryEvent(assemblyLine.getSubsystem(), EventKind.ROBOTARMS_BROKEN, this);
				assemblyLine.notifySubsystem(broken);
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
		return materialAmount;
	}

	@Override
	public Position getPosition() {
		return position;
	}
	
	private long lastLackOfMaterialSent = 0;
	
	@Override
	public void start() {
		if(status() == SubsystemStatus.WAITING) {
			if(type == RobotType.SCREWDRIVER || type ==  RobotType.PAINTER) {
				
				materialAmount -= 5;
				
				if(materialAmount < 10 && (System.currentTimeMillis() - lastLackOfMaterialSent) > 50000) {
					lastLackOfMaterialSent = System.currentTimeMillis();
					FactoryEvent lowmat = new FactoryEvent(assemblyLine.getSubsystem(), EventKind.ROBOTARMS_LACK_OF_MATERIAL, materialType, this);
					assemblyLine.notifySubsystem(lowmat);
				}
			} else if(type == RobotType.INSPECTOR) {
				if(Math.random() < 0.95) {
					if(materialType != null)
					{
						switch(materialType) {
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
					FactoryEvent done = new FactoryEvent(assemblyLine.getSubsystem(), EventKind.CAR_FINISHED, car, assemblyLine.getOutputBox());
					assemblyLine.notifySubsystem(done);
				}
			}
			
			timestamp = System.currentTimeMillis(); //The Robot takes 5 seconds to perform it's task
		}
		status();
	}
	
	@Override
	public void draw(Graphics g) {		
		//draw border
		if (type == RobotType.PAINTER)
			g.setColor(materialType.toColor());
		else
			g.setColor(Color.LIGHT_GRAY);
		g.fillRect(1, 1, position.xSize - 1, position.ySize - 1);
		g.setColor(Constants.UI_BORDER_COLOR);
		g.drawRect(0, 0, position.xSize, position.ySize);
		
		//draw text
		if (type == RobotType.PAINTER && materialType == Material.COLOR_WHITE)
			g.setColor(Color.BLACK);
		else
			g.setColor(Color.WHITE);
		
		Font prevFont = g.getFont();
		g.setFont(prevFont.deriveFont(9f));
		g.drawString(type.displayName, 1, position.ySize/4);
		
		g.setFont(prevFont);
		if (type != RobotType.INSPECTOR)
			g.drawString(""+materialAmount, position.xSize/3, (int) (position.ySize/1.5f));
	}

	@Override
	public String toString() {
		return String.format("(Robot \"%s\" at %s)", type.displayName, position.toString());
	}
	
}
