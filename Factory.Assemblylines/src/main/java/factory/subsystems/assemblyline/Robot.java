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
import factory.subsystems.assemblyline.interfaces.RobotInterface;

class Robot implements RobotInterface {

	/** Time in ms how long the robot takes to perform work. */
	private static final long SIMULATED_WORK_DURATION = 10000;
	
	private final RobotType robotType;
	private final Position position;
	private final AssemblyLine assemblyLine;
	
	private Material materialType;
	private int materialAmount;
	
	private ComponentStatus status = ComponentStatus.READY;
	private long lastLackOfMaterialEventSent = 0;
	
	public Robot(AssemblyLine assemblyLine, Position pos, RobotType type, Material materialType, int initialMaterialAmount) {
		this.robotType = type;
		this.position = pos;
		this.assemblyLine = assemblyLine;
		this.materialType = materialType;
		this.materialAmount = initialMaterialAmount;
	}

	@Override
	public boolean isReady() {
		return status == ComponentStatus.READY;
	}

	@Override
	public void start() {
		if (!isReady())
			throw new IllegalArgumentException("Robot started even though it was not ready!");
		
		Thread robotWorkThread = new Thread(() -> {
			try {
				status = ComponentStatus.WORKING;
				doWork();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		robotWorkThread.setDaemon(true);
		robotWorkThread.start();
	}
	
	private void doWork() throws InterruptedException {
		if (robotType != RobotType.INSPECTOR) {
			materialAmount -= robotType.materialsUsed;
			
			if (materialAmount < (robotType.materialsUsed * 5) && (System.currentTimeMillis() - lastLackOfMaterialEventSent) > 50000) {
				lastLackOfMaterialEventSent = System.currentTimeMillis();
				FactoryEvent lowMaterialsEvent = new FactoryEvent(
					assemblyLine.getSubsystem(),
					EventKind.LACK_OF_MATERIAL, 
					materialType, 
					this
				);
				assemblyLine.notifySubsystem(lowMaterialsEvent);
			}
		} else {
			if (Math.random() < 0.95) {
				FactoryEvent carFinishedEvent = new FactoryEvent(
					assemblyLine.getSubsystem(), 
					EventKind.CAR_FINISHED, 
					colorToCarMaterial(materialType), 
					assemblyLine.getOutputBox()
				);
				assemblyLine.notifySubsystem(carFinishedEvent);
			}
		}
		Thread.sleep(SIMULATED_WORK_DURATION);
		
		if (materialAmount >= robotType.materialsUsed) {
			status = ComponentStatus.READY;
		} else {
			status = ComponentStatus.OUT_OF_MATERIAL;
		}
	}
	
	/**
	 * Returns the appropriate "CAR_X" Material for the supplied "COLOR_X" Material.
	 */
	private Material colorToCarMaterial(Material color) {
		switch (materialType) {
			case COLOR_BLACK:
				return Material.CAR_BLACK;
			case COLOR_BLUE:
				return Material.CAR_BLUE;
			case COLOR_GRAY:
				return Material.CAR_GRAY;
			case COLOR_GREEN:
				return Material.CAR_GREEN;
			case COLOR_RED:
				return Material.CAR_RED;
			case COLOR_WHITE:
				return Material.CAR_WHITE;
			default:
				return Material.CAR_BLACK;
		}
	}
	
	@Override
	public void receiveContainer(Container container) {
		materialAmount += Objects.requireNonNull(container).getAmount();
		if (status == ComponentStatus.OUT_OF_MATERIAL) {
			status = ComponentStatus.READY;
		}
	}
	
	@Override
	public int getMaterialAmount() {
		return materialAmount;
	}
	
	@Override
	public Material getMaterialType() {
		return materialType;
	}

	@Override
	public RobotType getRobotType() {
		return robotType;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void draw(Graphics g) {
		//draw border
		if (robotType == RobotType.PAINTER)
			g.setColor(materialType.toColor());
		else
			g.setColor(Color.LIGHT_GRAY);
		g.fillRect(1, 1, position.xSize - 1, position.ySize - 1);
		g.setColor(Constants.UI_BORDER_COLOR);
		g.drawRect(0, 0, position.xSize, position.ySize);
		
		//draw text
		if (robotType == RobotType.PAINTER && materialType == Material.COLOR_WHITE)
			g.setColor(Color.BLACK);
		else
			g.setColor(Color.WHITE);
		
		Font prevFont = g.getFont();
		g.setFont(prevFont.deriveFont(9f));
		g.drawString(robotType.displayName, 1, position.ySize/4);
		
		g.setFont(prevFont);
		if (robotType != RobotType.INSPECTOR)
			g.drawString(""+materialAmount, position.xSize/3, (int) (position.ySize/1.5f));
	}

}
