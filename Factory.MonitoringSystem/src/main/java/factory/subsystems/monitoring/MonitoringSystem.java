package factory.subsystems.monitoring;

import static java.util.logging.Level.SEVERE;

import java.util.Objects;
import java.util.logging.Logger;

import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.enums.Material;
import factory.shared.enums.MaterialStatus;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.GUIHandler;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.assemblyline.AssemblyLineSystem;
import factory.subsystems.monitoring.interfaces.EventHandlerInterface;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.warehouse.WarehouseSystem;

public class MonitoringSystem implements MonitoringInterface {
	
	private static final Logger LOGGER = Logger.getLogger(MonitoringSystem.class.getName());

	
	private GUIHandler guiHandler;
	
	private EventHandlerInterface eventHandler;

	private SubsystemStatus status;
	private AgvCoordinator agvSystem;
	private WarehouseSystem warehouseSystem;
	private AssemblyLineSystem alsubsys;

	private ResourceBox shippingBox;
	private Position staffQuarterPosition;
	

	@Override
	public synchronized void handleEvent(FactoryEvent event) {
		eventHandler.handleEvent(event);
	}

	@Override
	public void start() {
		Objects.requireNonNull(this.agvSystem);
		Objects.requireNonNull(this.warehouseSystem);
		Objects.requireNonNull(this.alsubsys);
		
		this.eventHandler = new AutonomousEventHandler(this);
		
		new Thread(() -> {
			this.agvSystem.start();
		}).start();

		new Thread(() -> {
			this.warehouseSystem.start();
		}).start();

		new Thread(() -> {
			this.alsubsys.start();
		}).start();

		this.guiHandler.start();
		this.setStatus(SubsystemStatus.RUNNING);

	}

	@Override
	public void stop() {
		try {
			if (this.agvSystem != null)
				this.agvSystem.stop();
			if (this.warehouseSystem != null)
				this.warehouseSystem.stop();
			if (this.alsubsys != null)
				this.alsubsys.stop();
		} catch (Exception ex) {
			LOGGER.log(SEVERE, ex.toString(), ex);
		}
		this.setStatus(SubsystemStatus.STOPPED);
	}



	@Override
	public SubsystemStatus getStatus() {
		return this.status;
	}

	@Override
	public void setStatus(SubsystemStatus status) {
		this.status = status;
	}

	@Override
	public AgvCoordinator getAgvSystem() {
		return agvSystem;
	}

	@Override
	public void setAgvSystem(AgvCoordinator agvSystem) {
		this.guiHandler.addToFactoryPanel(agvSystem);
		this.agvSystem = agvSystem;
	}

	@Override
	public WarehouseSystem getWarehouseSystem() {
		return warehouseSystem;
	}

	@Override
	public void setWarehouseSystem(WarehouseSystem warehouseSystem) {
		this.guiHandler.addToFactoryPanel(warehouseSystem);
		this.warehouseSystem = warehouseSystem;
	}

	@Override
	public ResourceBox getShippingBox() {
		return shippingBox;
	}

	/** Only call this after WarehouseSystem has been set. */
	@Override
	public void setShippingBox(Position shippingBoxPos) {
		ResourceBox box = new ResourceBox(warehouseSystem, shippingBoxPos) {
			@Override
			public void receiveContainer(Container container) {
				super.receiveContainer(container);
				
				//simulate delivery of containers after a certain fullness is reached
				if (getFullness() == MaterialStatus.BAD) {
					Material[] storedMaterials = getStoredMaterials();
					
					for (Material material : storedMaterials) {
						try {
							deliverContainer(material);
							Thread.sleep(300);	//simulates time to load container into truck
						} catch (IllegalArgumentException e) {
							//all Containers of this Material-Type have been delivered.
							continue;	
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		this.shippingBox = box;
	}

	@Override
	public AssemblyLineSystem getALSubsys() {
		return alsubsys;
	}

	@Override
	public void setAssemblyLine(AssemblyLineSystem assemblyLine) {
		this.guiHandler.addToFactoryPanel(assemblyLine);
		this.alsubsys = assemblyLine;
	}



	@Override
	public Position getStaffQuarterPosition() {
		return staffQuarterPosition;
	}

	@Override
	public void setStaffQuarterPosition(Position staffQuarterPosition) {
		this.staffQuarterPosition = staffQuarterPosition;
	}

	@Override
	public void setGUIHandler(GUIHandler handler) {
		this.guiHandler = handler;
	}

}
