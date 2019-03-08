package factory.subsystems.monitoring;

import static factory.shared.enums.EventKind.*;
import static factory.shared.enums.EventKind.EventSeverity.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;
import java.util.logging.Logger;

import factory.shared.Constants;
import factory.shared.FactoryEvent;
import factory.shared.ResourceBox;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;
import factory.shared.enums.EventKind.EventSeverity;
import factory.subsystems.agv.AgvTask;
import factory.subsystems.monitoring.interfaces.ErrorHandlerInterface;
import factory.subsystems.monitoring.interfaces.EventHandlerInterface;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.warehouse.WarehouseTask;

/**
 * Handles events autonomously (in it's own thread).
 */
public class AutonomousEventHandler implements EventHandlerInterface {

	// ---------------------------------- Interface -----------------------------------
	
	@Override
	public void handleEvent(FactoryEvent event) {
		try {
			queuedEvents.put(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// -------------------------------- Implementation --------------------------------
	
	private static final Logger LOGGER = Logger.getLogger(MonitoringSystem.class.getName());
	
	private static final long DEADLINE_NORMAL = 300000L;
	private static final long DEADLINE_IMPORTANT = 60000L;
	
	/** Queue of events that is ordered by their severity. */
	private final BlockingQueue<FactoryEvent> queuedEvents = new PriorityBlockingQueue<>(11, (ev1, ev2) -> {
		return ev1.getKind().severity.ordinal() - ev2.getKind().severity.ordinal();
	});
	
	/** Consumes events on the basis of their {@link EventSeverity}. */
	private final Map<EventSeverity, Consumer<FactoryEvent>> severityConsumers;
	
	/** Consumes events of {@link EventSeverity#INFO}. */
	private final Map<EventKind, Consumer<FactoryEvent>> infoConsumers;
	
	/** Consumes events of {@link EventSeverity#NORMAL}. */
	private final Map<EventKind, Consumer<FactoryEvent>> normalConsumers;
	
	/** Consumes events of {@link EventSeverity#IMPORTANT}. */
	private final Map<EventKind, Consumer<FactoryEvent>> importantConsumers;
	
	private final TaskChainer taskChainer;
	
	private final MonitoringInterface monitor;
	private final ErrorHandlerInterface errorHandler;
	
	public AutonomousEventHandler(MonitoringInterface monitor) {
		this.monitor = monitor;
		this.errorHandler = new ErrorEventHandler(monitor);
		this.taskChainer = new TaskChainer(monitor.getWarehouseSystem(), monitor.getAgvSystem());
		
		this.severityConsumers = initializeSeverityConsumers();
		this.infoConsumers = initializeInfoConsumers();
		this.normalConsumers = initializeNormalConsumers();
		this.importantConsumers = initializeImportantConsumers();
		
		Thread thread = new Thread(() -> {
			try {
				handleEventLoop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	
	
	/**
	 * Starts infinite loop handling events that were put into the {@link #queuedEvents} queue.<br>
	 * Loop can only be stopped if thread dies -> make sure to run this in a Daemon thread!
	 * 
	 * @throws InterruptedException if the Thread is interrupted while waiting for a FactoryEvent.
	 */
	private void handleEventLoop() throws InterruptedException {
		while (true) {
			FactoryEvent event = queuedEvents.take();	//blocks until event is received
			EventSeverity severity = event.getKind().severity;
			
			Consumer<FactoryEvent> severityConsumer = Objects.requireNonNull(
				severityConsumers.get(severity), 
				"Unhandled EventSeverity: " + severity
			);
			if (Constants.DEBUG) 
				System.out.println("[handling] " + event);
			severityConsumer.accept(event);
		}
	}
	
	

	private Map<EventSeverity, Consumer<FactoryEvent>> initializeSeverityConsumers() {
		final Map<EventSeverity, Consumer<FactoryEvent>> map = new HashMap<>();
		
		map.put(GLOBAL_EROR, ev -> errorHandler.handleGlobalError(ev));
		
		map.put(ERROR, ev -> errorHandler.handleError(ev.getSource(), ev.getKind()));
		
		map.put(IMPORTANT, ev -> {
			Consumer<FactoryEvent> consumer = Objects.requireNonNull(
				importantConsumers.get(ev.getKind()), 
				"Unhandled EventKind: " + ev.getKind()
			);
			consumer.accept(ev);
		});
		
		map.put(NORMAL, ev -> {
			Consumer<FactoryEvent> consumer = Objects.requireNonNull(
				normalConsumers.get(ev.getKind()), 
				"Unhandled EventKind: " + ev.getKind()
			);
			consumer.accept(ev);
		});
		
		map.put(INFO, ev -> {
			Consumer<FactoryEvent> consumer = Objects.requireNonNull(
				infoConsumers.get(ev.getKind()), 
				"Unhandled EventKind: " + ev.getKind()
			);
			consumer.accept(ev);
		});
		
		return Collections.unmodifiableMap(map);
	}
	
	
	
	private Map<EventKind, Consumer<FactoryEvent>> initializeInfoConsumers() {
		final Map<EventKind, Consumer<FactoryEvent>> map = new HashMap<>();
		
		map.put(TASK_NOT_COMPLETED_BEFORE_DEADLINE, event -> {
			LOGGER.info("Did not complete " + event.getAttachment(0) + " before deadline!");
		});
		
		return Collections.unmodifiableMap(map);
	}
	
	
	
	private Map<EventKind, Consumer<FactoryEvent>> initializeNormalConsumers() {
		final Map<EventKind, Consumer<FactoryEvent>> map = new HashMap<>();
		
		map.put(AGV_CONTAINER_DELIVERED, event -> {
			//do nothing
		});
		
		map.put(WAREHOUSE_TASK_COMPLETED, event -> {
			WarehouseTask task = (WarehouseTask) event.getAttachment(0);
			ContainerSupplier supplier = (ContainerSupplier) event.getAttachment(1);
			
			taskChainer.notifyTaskCompleted(task.getId(), null, agvTask -> {
				if (agvTask.getPickup() == null) {
					agvTask.setPickup(supplier);
				}
			});
		});
		
		map.put(WAREHOUSE_NEW_TRANSACTION, event -> {
			LOGGER.info("Transaction happened: " + event.getAttachment(0));
		});
		
		map.put(CAR_FINISHED, event -> {
			Material material = (Material) event.getAttachment(0);
			ContainerSupplier supplier = (ContainerSupplier) event.getAttachment(1);
			
			AgvTask agvtask = new AgvTask(DEADLINE_NORMAL, material, supplier, monitor.getShippingBox());
			monitor.getAgvSystem().receiveTask(agvtask);
		});
		
		return Collections.unmodifiableMap(map);
	}
	
	
	
	private Map<EventKind, Consumer<FactoryEvent>> initializeImportantConsumers() {
		final Map<EventKind, Consumer<FactoryEvent>> map = new HashMap<>();
		
		map.put(RESOURCEBOX_ALMOST_FULL, event -> {
			ResourceBox box = (ResourceBox) event.getAttachment(0);
			LOGGER.warning(box + " is almost full!");
		});
		
		map.put(LACK_OF_MATERIAL, event -> {
			Material material = (Material) event.getAttachment(0);
			ContainerDemander robot = (ContainerDemander) event.getAttachment(1);

			handleBasicSupplyAndDemand(robot, material, DEADLINE_IMPORTANT, DEADLINE_IMPORTANT);
		});
		
		map.put(AGV_PATHING_IMPOSSIBLE, event -> {
			AgvTask task = (AgvTask) event.getAttachment(0);
			LOGGER.warning("Could not path from " + task.getPickup() + " to: " + task.getDropoff());
		});
		
		return Collections.unmodifiableMap(map);
	}
	
	
	
	/**
	 * Creates and starts the basic task chain for a {@link ContainerDemander} requesting some {@link Material}.
	 * 
	 * @param demander - The demander in need of a specific material.
	 * @param material - The material needed.
	 * @param warehouseDeadline - the deadline for the Warehouse-Subsystem to finish it's Task.
	 * @param agvDeadline - the deadline for the Agv-Subsystem to finish it's Task (after the warehouse's task).
	 */
	private void handleBasicSupplyAndDemand(ContainerDemander demander, Material material, long warehouseDeadline, long agvDeadline) {
		WarehouseTask warehouseTask = new WarehouseTask(warehouseDeadline, material);
		monitor.getWarehouseSystem().receiveTask(warehouseTask);
		
		//supplier for this AgvTask will be set on the WarehouseTask's completed event
		AgvTask followupAgvTask = new AgvTask(warehouseDeadline + agvDeadline, material, null, demander);
		taskChainer.registerFollowupTask(warehouseTask.getId(), followupAgvTask);
	}
	
}
