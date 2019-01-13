package factory.subsystems.monitoring;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import factory.shared.AbstractSubsystem;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.enums.EventKind;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.agv.Forklift;

public class MonitoringSystemTest {


	@Spy
	private MonitoringSystem monitor;

	private ErrorEventHandler errorEventHandler;

	@BeforeMethod
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
		this.errorEventHandler = Mockito.spy(new ErrorEventHandler(monitor));
		Mockito.doReturn(this.errorEventHandler).when(monitor).getErrorHandler();
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
		AbstractSubsystem subsystem = Mockito.mock(AgvCoordinator.class);
		EventKind agvForkliftDamaged = EventKind.AGV_FORKLIFT_DAMAGED;
		
		Forklift damagedForklift = new Forklift(new Position(100,100));
		FactoryEvent event = new FactoryEvent(subsystem, agvForkliftDamaged,damagedForklift);

		this.monitor.handleEvent(event);
		Mockito.verify(this.errorEventHandler, Mockito.times(1)).handleError(subsystem, agvForkliftDamaged);
		Mockito.verify(this.errorEventHandler, Mockito.never()).handleGlobalError(event);
	}

	@Test(description = "verifies that the error handler is not used when the event is not of type error or global error")
	public void handleEventCheckThatErrorEventHandlerIsNotUsed() {
		AbstractSubsystem subsystem = Mockito.mock(AgvCoordinator.class);
		FactoryEvent event = new FactoryEvent(subsystem, EventKind.CONVEYORS_LACK_OF_OIL);

		this.monitor.handleEvent(event);
		Mockito.verifyZeroInteractions(this.errorEventHandler);
	}
	
	
  @Test
  public void addOrder() {
//    this.monitor.addOrder(order);
//    this.monitor.get
	  
	  
  }

  @Test
  public void handleEvent() {
    
  }

  @Test
  public void handleEventHandlingException() {
    
  }

  @Test
  public void handleNewOrder() {
    
  }

  @Test
  public void handleNormalEvent() {
    
  }

  @Test
  public void start() {
    
  }

  @Test
  public void stop() {
    
  }
}
