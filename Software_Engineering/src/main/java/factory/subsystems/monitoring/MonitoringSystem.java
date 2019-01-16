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
import factory.shared.Constants;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.enums.EventKind;
import factory.shared.enums.EventKind.EventSeverity;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;
import factory.shared.interfaces.Monitorable;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.agv.AgvTask;
import factory.subsystems.assemblyline.AL_Subsystem;
import factory.subsystems.assemblyline.Robot;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.monitoring.onlineshop.OnlineShopUser;
import factory.subsystems.monitoring.onlineshop.Order;
import factory.subsystems.warehouse.WarehouseSystem;
import factory.subsystems.warehouse.WarehouseTask;

public class MonitoringSystem implements MonitoringInterface {
	
	private static final Logger LOGGER = Logger.getLogger(MonitoringSystem.class.getName());

	private static final int NORMAL_SEVERITY_DEADLINE = 80000;
	private static final int IMPORTANT_SEVERITY_DEADLINE = 30000;

	private final UserHandler userHandler;
	private final GUIHandler handler;
	private final ErrorEventHandler errorHandler;

	private final Map<OnlineShopUser, List<Order>> orderMap;

	private SubsystemStatus status;
	private AgvCoordinator agvSystem;
	private WarehouseSystem warehouseSystem;
	private AL_Subsystem alsubsys;
	private OnlineShop onlineShop;

	private ResourceBox shippingBox;
	private Position staffQuarterPosition;

	/**
	 * this map stores the info to which ContainerDemander the prepared material
	 * should be transported
	 */
	private final Map<WarehouseTask, ContainerDemander> warehouseTaskDemanders;

	public MonitoringSystem() {
		this(new UIConfiguration(1000, 1000));
	}

	public MonitoringSystem(UIConfiguration uiConfig) {
		this.handler = new GUIHandler(this, uiConfig);
		this.errorHandler = new ErrorEventHandler(this);
		this.orderMap = new HashMap<>();
		this.warehouseTaskDemanders = new HashMap<>();
		this.onlineShop = new OnlineShop(this);
		this.userHandler = new UserHandler();
	}

	@Override
	public synchronized void handleEvent(FactoryEvent event) {
		try {
			if (Constants.DEBUG)
				System.out.println("-------> handling event: " + event);
			
			Monitorable source = event.getSource();
			EventKind eventKind = event.getKind();
			EventSeverity severity = eventKind.severity;
			
			switch (severity) {
			case GLOBAL_EROR:
				this.getErrorHandler().handleGlobalError(event);
				this.setStatus(SubsystemStatus.BROKEN);
				break;
			case ERROR:
				getErrorHandler().handleError(source, eventKind);
				break;
			case INFO:
				switch (event.getKind()) {
				case TASK_NOT_COMPLETED_BEFORE_DEADLINE:
					LOGGER.info("Did not complete " + event.getAttachment(0) + " before deadline!");
					break;
				default: 
					if (Constants.DEBUG)
						System.out.println("HANDLEEVENT " + event + " NOT IMPLEMENTED");
					break;
				}
				break;
			case IMPORTANT:
				switch (event.getKind()) {
				case RESOURCEBOX_ALMOST_FULL:
					ResourceBox box = (ResourceBox) event.getAttachment(0);
					LOGGER.warning(box + " is almost full!");
					break;
				case ROBOTARMS_LACK_OF_MATERIAL:
					Material material = (Material) event.getAttachment(0);
					Robot robot = (Robot) event.getAttachment(1);

					WarehouseTask wht = new WarehouseTask(IMPORTANT_SEVERITY_DEADLINE, material);
					warehouseTaskDemanders.put(wht, robot);
					this.warehouseSystem.receiveTask(wht);
					break;
				case AGV_PATHING_IMPOSSIBLE:
					AgvTask task = (AgvTask) event.getAttachment(0);
					LOGGER.warning("Could not path from " + task.getPickup() + " to: " + task.getDropoff());
					break;
				default: 
					if (Constants.DEBUG)
						System.out.println("HANDLEEVENT " + event + " NOT IMPLEMENTED");
					break;
				}
				break;
			case NORMAL:
				// handleNormalEvent(event);
				switch (event.getKind()) {
				case AGV_CONTAINER_DELIVERED:
					if (Constants.DEBUG)
						System.out.println("AGV_CONTAINER_DELIVERED");
					// do nothing
					break;
				case WAREHOUSE_TASK_COMPLETED:
					if (Constants.DEBUG)
						System.out.println("WAREHOUSE_TASK_COMPLETED");
					WarehouseTask task = (WarehouseTask) event.getAttachment(0);
					Material mat = task.getMaterial();
					ContainerSupplier supplier = (ContainerSupplier) event.getAttachment(1);
					ContainerDemander demander = getWarehouseTaskDemanders().get(task);
					
					if (demander == null) {
						LOGGER.warning("no demander for the warehousetask found");
					} else {
						AgvTask newTask = new AgvTask(IMPORTANT_SEVERITY_DEADLINE, mat, supplier, demander);
						agvSystem.submitTask(newTask);
					}
					break;
				case CAR_FINISHED: // Created new EventKind if the Box is full -Max
					handleCarFinishedEvent(event);
					break;
				default:
					if (Constants.DEBUG)
						System.out.println("HANDLEEVENT " + event + "NOT IMPLEMENTED");
					break;
				}
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			handleEventHandlingException(event, ex);
		}
	}

	private void handleCarFinishedEvent(FactoryEvent event) {
		System.out.println("CAR_FINISHED");
		Material material = (Material) event.getAttachment(0);
		ContainerSupplier supplier = (ContainerSupplier) event.getAttachment(1);
		
		AgvTask agvtask = new AgvTask(NORMAL_SEVERITY_DEADLINE, material, supplier, shippingBox);
		System.out.println("SUBMITTING" + agvtask);
		agvSystem.submitTask(agvtask);
	}

	@Override
	public void start() {
		Objects.requireNonNull(this.agvSystem);// TODO @thomas throw exception
		Objects.requireNonNull(this.warehouseSystem);
		Objects.requireNonNull(this.alsubsys);

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

	private void handleEventHandlingException(FactoryEvent event, Exception ex) {
		LOGGER.log(SEVERE, ex.toString(), ex);
		getErrorHandler().handleGlobalError(event);
		this.setStatus(SubsystemStatus.BROKEN);
	}

	ErrorEventHandler getErrorHandler() {
		return errorHandler;
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
	public AL_Subsystem getALSubsys() {
		return alsubsys;
	}

	@Override
	public void setAssemblyLine(AL_Subsystem assemblyLine) {
		this.handler.addToFactoryPanel(assemblyLine);
		this.alsubsys = assemblyLine;
	}

	@Override
	public Map<OnlineShopUser, List<Order>> getOrderMap() {
		return orderMap;
	}

	public Map<WarehouseTask, ContainerDemander> getWarehouseTaskDemanders() {
		return warehouseTaskDemanders;
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
