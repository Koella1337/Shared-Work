package factory.subsystems.monitoring;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import app.gui.GUIHandler;
import factory.shared.AbstractSubsystem;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.enums.EventKind;
import factory.shared.enums.EventKind.EventSeverity;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.ContainerSupplier;
import factory.shared.interfaces.Monitorable;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.agv.AgvTask;
import factory.subsystems.assemblyline.AssemblyLine;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.monitoring.onlineshop.Order;
import factory.subsystems.warehouse.WarehouseSystem;
import factory.subsystems.warehouse.WarehouseTask;

public class MonitoringSystem implements MonitoringInterface {
	private static final Logger LOGGER = Logger.getLogger(MonitoringSystem.class.getName());

	private final GUIHandler handler;
	private final ErrorEventHandler errorHandler;

	private final List<Order> orderList;

	private SubsystemStatus status;
	private AgvCoordinator agvSystem;
	private WarehouseSystem warehouseSystem;
	private AssemblyLine assemblyLine;

	private ResourceBox shippingBox = new ResourceBox(new Position(10, 10));


	public MonitoringSystem() {
		this.handler = new GUIHandler(this);
		this.errorHandler = new ErrorEventHandler(this);
		this.orderList = new ArrayList<>();
	}

	@Override
	public synchronized void handleEvent(FactoryEvent event) {
		try {
			LOGGER.log(INFO, String.format("handling event %s ...", event));
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
			case IMPORTANT:
				break;
			case INFO:
				break;
			case NORMAL:
				// handleNormalEvent(event);
				switch (event.getKind()) {
				case AGV_CONTAINER_DELIVERED:
					System.out.println("AGV_CONTAINER_DELIVERED");
					// AgvTask task = (AgvTask) event.getAttachment(1);
					break;
				case WAREHOUSE_TASK_COMPLETED:
					System.out.println("WAREHOUSE_TASK_COMPLETED");
					WarehouseTask task = (WarehouseTask) event.getAttachment(0);
					Material mat = task.material;
					ContainerSupplier box = (ContainerSupplier) event.getAttachment(1);

					AgvTask agv = new AgvTask(mat, box, assemblyLine);
					
					
					//AgvTask t = new AgvTask(mat, new ResourceBox(new Position(0,0)), new ResourceBox(new Position(1000,1000)));
					agvSystem.submitTask( agv);
					break;
				case CAR_FINISHED:
					System.out.println("CAR_FINISHED");
					Material material = (Material) event.getAttachment(0);
					AgvTask agvtask = new AgvTask(material, this.assemblyLine.conveyor.getOutputbox(), shippingBox);
					agvSystem.submitTask(agvtask);
				default:
					break;
				}

				break;
			default:
				break;
			}
		} catch (Exception ex) {
			handleEventHandlingException(event, ex);
		}
	}


	@Override
	public void start() {
		Objects.requireNonNull(this.agvSystem);// TODO @thomas throw exception
		Objects.requireNonNull(this.warehouseSystem);

		this.agvSystem.start();
		this.warehouseSystem.start();

		this.handler.start();
		this.setStatus(SubsystemStatus.RUNNING);
	}

	@Override
	public void stop() {
		try {
			if (this.agvSystem != null)
				this.agvSystem.stop();
			if (this.warehouseSystem != null)
				this.warehouseSystem.stop();
		} catch (Exception ex) {
			LOGGER.log(SEVERE, ex.toString(), ex);
		}
		this.setStatus(SubsystemStatus.STOPPED);
	}

	@Override
	public void addOrder(Order order) {
		LOGGER.log(INFO, "order placed: " + order);
		this.orderList.add(order);
		handleNewOrder(order);
	}

	private void handleNewOrder(Order order) {
		this.orderList.add(order);
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
		LOGGER.log(INFO, String.format("Status set to %s", status));
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

	@Override
	public void setShippingBox(ResourceBox shippingBox) {
		this.shippingBox = shippingBox;
	}

	@Override
	public AssemblyLine getAssemblyLine() {
		return assemblyLine;
	}

	@Override
	public void setAssemblyLine(AssemblyLine assemblyLine) {
		this.assemblyLine = assemblyLine;
	}

}
