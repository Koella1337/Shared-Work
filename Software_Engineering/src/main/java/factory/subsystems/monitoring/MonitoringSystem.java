package factory.subsystems.monitoring;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import app.gui.GUIHandler;
import app.gui.OnlineShop;
import app.gui.UIConfiguration;
import factory.shared.AbstractSubsystem;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.enums.SubsystemStatus;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.assemblyline.AssemblyLineSystem;
import factory.subsystems.monitoring.interfaces.EventHandlerInterface;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.monitoring.onlineshop.OnlineShopUser;
import factory.subsystems.monitoring.onlineshop.Order;
import factory.subsystems.warehouse.WarehouseSystem;

public class MonitoringSystem implements MonitoringInterface {
	
	private static final Logger LOGGER = Logger.getLogger(MonitoringSystem.class.getName());

	private final Map<OnlineShopUser, List<Order>> orderMap;
	
	private final UserHandler userHandler;
	private final GUIHandler handler;
	
	private EventHandlerInterface eventHandler;

	private SubsystemStatus status;
	private AgvCoordinator agvSystem;
	private WarehouseSystem warehouseSystem;
	private AssemblyLineSystem alsubsys;
	private OnlineShop onlineShop;

	private ResourceBox shippingBox;
	private Position staffQuarterPosition;

	public MonitoringSystem() {
		this(new UIConfiguration(1000, 1000));
	}

	public MonitoringSystem(UIConfiguration uiConfig) {
		this.handler = new GUIHandler(this, uiConfig);
		this.orderMap = new HashMap<>();
		this.onlineShop = new OnlineShop(this);
		this.userHandler = new UserHandler();
	}

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

		this.handler.start();
		this.setStatus(SubsystemStatus.RUNNING);

		new Thread(() -> {
			this.onlineShop.start();
		}).start();
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
	public void addOrder(Order order) throws InvalidOrderException {
		LOGGER.log(INFO, "order placed: " + order);
		OnlineShopUser user = order.getUser();
		
		if(!userHandler.loginCorrect(user)) {
			throw new InvalidOrderException("The password is incorrect or the values are invalid!");
		}

		if (this.orderMap.get(user) == null) {
			this.orderMap.put(user, new ArrayList<Order>(Arrays.asList(order)));
		} else {
			this.orderMap.get(user).add(order);
		}
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
	public void setCurrentSubsystemToShow(AbstractSubsystem subsystem) {
		this.handler.setCurrentSubsystem(subsystem);
	}

	@Override
	public AgvCoordinator getAgvSystem() {
		return agvSystem;
	}

	@Override
	public void setAgvSystem(AgvCoordinator agvSystem) {
		this.handler.addToFactoryPanel(agvSystem);
		this.agvSystem = agvSystem;
	}

	@Override
	public WarehouseSystem getWarehouseSystem() {
		return warehouseSystem;
	}

	@Override
	public void setWarehouseSystem(WarehouseSystem warehouseSystem) {
		this.handler.addToFactoryPanel(warehouseSystem);
		this.warehouseSystem = warehouseSystem;
	}

	@Override
	public ResourceBox getShippingBox() {
		return shippingBox;
	}

	/** Only call this after WarehouseSystem has been set. */
	@Override
	public void setShippingBox(Position shippingBoxPos) {
		ResourceBox box = new ResourceBox(warehouseSystem, shippingBoxPos);
		this.shippingBox = box;
	}

	@Override
	public AssemblyLineSystem getALSubsys() {
		return alsubsys;
	}

	@Override
	public void setAssemblyLine(AssemblyLineSystem assemblyLine) {
		this.handler.addToFactoryPanel(assemblyLine);
		this.alsubsys = assemblyLine;
	}

	@Override
	public Map<OnlineShopUser, List<Order>> getOrderMap() {
		return orderMap;
	}

	@Override
	public Position getStaffQuarterPosition() {
		return staffQuarterPosition;
	}

	@Override
	public void setStaffQuarterPosition(Position staffQuarterPosition) {
		this.staffQuarterPosition = staffQuarterPosition;
	}

}
