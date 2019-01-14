package factory.subsystems.monitoring;

import static org.testng.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import factory.shared.AbstractSubsystem;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.agv.AgvTask;
import factory.subsystems.agv.Forklift;
import factory.subsystems.assemblyline.AssemblyLine;
import factory.subsystems.assemblyline.Conveyor;
import factory.subsystems.assemblyline.Robot;
import factory.subsystems.assemblyline.RobotTypes;
import factory.subsystems.warehouse.WarehouseSystem;
import factory.subsystems.warehouse.WarehouseTask;

public class MonitoringSystemTest {

	@Spy
	private MonitoringSystem monitor;

	@Mock
	private AgvCoordinator agvSystem;
	@Mock
	private AssemblyLine assemblyLineSystem;
	@Mock
	private WarehouseSystem warehouseSystem;

	private ErrorEventHandler errorEventHandler;

	@BeforeMethod
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
		this.errorEventHandler = Mockito.spy(new ErrorEventHandler(monitor));
		Mockito.doReturn(this.errorEventHandler).when(monitor).getErrorHandler();

		this.monitor.setWarehouseSystem(warehouseSystem);
		this.monitor.setAgvSystem(agvSystem);
		this.monitor.setAssemblyLine(assemblyLineSystem);

		Conveyor conveyor = new Conveyor(this.assemblyLineSystem, new Position(10, 10), 5, 5);
		conveyor.setOutputbox(new ResourceBox(new Position(20, 20)));
		Mockito.doReturn(conveyor).when(this.assemblyLineSystem).getConveyor();

	}

	@Test(description = "verifies if only the correct method of the error handler is called when a global error occurs")
	public void handleGlobalError() {
		AgvCoordinator agvSystem = Mockito.mock(AgvCoordinator.class);
		this.monitor.setAgvSystem(agvSystem);

		FactoryEvent event = new FactoryEvent(agvSystem, EventKind.MONITORING_HANDLE_EVENT_FAILED);

		this.monitor.handleEvent(event);
		Mockito.verify(this.errorEventHandler, Mockito.times(1)).handleGlobalError(event);
		Mockito.verify(this.errorEventHandler, Mockito.never()).handleError(Mockito.any(), Mockito.any());
		Mockito.verify(agvSystem, Mockito.times(1)).stop();
	}

	@Test(description = "verifies if only the correct method of the error handler is called when a error occurs")
	public void handleError() {
		AgvCoordinator subsystem = Mockito.mock(AgvCoordinator.class);
		EventKind agvForkliftDamaged = EventKind.AGV_FORKLIFT_DAMAGED;

		Forklift damagedForklift = new Forklift(new Position(100, 100), subsystem);
		FactoryEvent event = new FactoryEvent(subsystem, agvForkliftDamaged, damagedForklift);

		this.monitor.handleEvent(event);
		Mockito.verify(this.errorEventHandler, Mockito.times(1)).handleError(subsystem, agvForkliftDamaged);
		Mockito.verify(this.errorEventHandler, Mockito.never()).handleGlobalError(event);
	}

	@Test(description = "verifies that the error handler is not used when the event is not of type error or global error")
	public void handleEventCheckThatErrorEventHandlerIsNotUsed() {
		AbstractSubsystem subsystem = Mockito.mock(AgvCoordinator.class);
		FactoryEvent event = new FactoryEvent(subsystem, EventKind.CONVEYORS_LACK_OF_OIL, Mockito.mock(Conveyor.class));

		this.monitor.handleEvent(event);
		Mockito.verifyZeroInteractions(this.errorEventHandler);
	}

	@Test(description = "verifies that the correct task is submitted to the agv system when a car finished event is received")
	public void testHandleCarFinishedEvent() {
		Position pos = new Position(10, 14);
		FactoryEvent event = new FactoryEvent(this.assemblyLineSystem, EventKind.CAR_FINISHED, Material.CAR,
				new Robot(RobotTypes.GRABBER, 1, pos, this.assemblyLineSystem));
		this.monitor.handleEvent(event);

		AgvTask agvTaskToSubmit = new AgvTask(Material.CAR, this.assemblyLineSystem.getConveyor().getOutputbox(),
				this.monitor.getShippingBox());
		ArgumentCaptor<AgvTask> argument = ArgumentCaptor.forClass(AgvTask.class);
		Mockito.verify(this.agvSystem, Mockito.times(1)).submitTask(argument.capture());

		AgvTask param = argument.getValue();
		assertEquals(param.getDropoff(), agvTaskToSubmit.getDropoff());
		assertEquals(param.getPickup(), agvTaskToSubmit.getPickup());
		assertEquals(param.getMaterial(), agvTaskToSubmit.getMaterial());
	}

	@Test(description = "verifies that the correct agv task is submitted to the agv system when a car finished event is received")
	public void testHandleWarehouseTaskCompletedEvent() {
		Position pos = new Position(1, 1);

		ContainerSupplier storageSite = Mockito.mock(ContainerSupplier.class);
		Mockito.doReturn(pos).when(storageSite).getPosition();

		WarehouseTask wht = new WarehouseTask(Material.CAR_BODIES);
		Robot robot  = Mockito.mock(Robot.class);
		HashMap<WarehouseTask, ContainerDemander> map = new HashMap<>(Collections.singletonMap(wht, robot));
		map.put(wht, robot);
		Mockito.doReturn(map).when(this.monitor).getWarehouseTaskDemanders();
		
		FactoryEvent event = new FactoryEvent(this.warehouseSystem, EventKind.WAREHOUSE_TASK_COMPLETED, wht,
				storageSite);
		this.monitor.handleEvent(event);

		ArgumentCaptor<AgvTask> argument = ArgumentCaptor.forClass(AgvTask.class);
		Mockito.verify(this.agvSystem, Mockito.times(1)).submitTask(argument.capture());

		AgvTask param = argument.getValue();
		assertEquals(param.getDropoff(), robot);
		assertEquals(param.getPickup(),storageSite);
		assertEquals(param.getMaterial(),Material.CAR_BODIES);
	}
	
	@Test(description = "verifies that the correct warehousetask submitted to the warehouse system when a ROBOTARMS_LACK_OF_MATERIAL event is received")
	public void testHandleRobotarmsLackOfMaterialEvent() {
		Position pos = new Position(2, 2);

		Robot robot  = Mockito.mock(Robot.class);
		Mockito.doReturn(pos).when(robot).getPosition();

		FactoryEvent event = new FactoryEvent(this.warehouseSystem, EventKind.ROBOTARMS_LACK_OF_MATERIAL, Material.CAR_BODIES,
				robot);
		WarehouseTask wht = new WarehouseTask(Material.CAR_BODIES);
		HashMap<WarehouseTask, ContainerDemander> map = new HashMap<>(Collections.singletonMap(wht, robot));
		Mockito.doReturn(map).when(this.monitor).getWarehouseTaskDemanders();
		
		this.monitor.handleEvent(event);
		

		ArgumentCaptor<WarehouseTask> argument = ArgumentCaptor.forClass(WarehouseTask.class);
		Mockito.verify(this.warehouseSystem, Mockito.times(1)).receiveTask(argument.capture());

		WarehouseTask param = argument.getValue();
		assertEquals(param.getMaterial(), Material.CAR_BODIES);
	}
	
	

}