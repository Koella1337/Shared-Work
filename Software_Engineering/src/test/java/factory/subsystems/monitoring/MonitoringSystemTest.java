package factory.subsystems.monitoring;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import factory.shared.AbstractSubsystem;
import factory.shared.FactoryEvent;
import factory.shared.enums.EventKind;
import factory.subsystems.agv.AgvCoordinator;

@Test(enabled = true, description = "basic test for the MonitoringSystem")
public class MonitoringSystemTest {

	@Spy
	private MonitoringSystem monitor;

	private ErrorEventHandler errorEventHandler;

	@BeforeMethod
	public void beforeMethod() {
		MockitoAnnotations.initMocks(this);
		this.errorEventHandler = spy(new ErrorEventHandler(monitor));
		doReturn(this.errorEventHandler).when(monitor).getErrorHandler();
	}

	@Test(description = "verifies if only the correct method of the error handler is called when a global error occurs")
	public void handleGlobalError() {
		AbstractSubsystem subsystem = mock(AgvCoordinator.class);
		this.monitor.addToSubsystemList(subsystem);
		
		FactoryEvent event = new FactoryEvent(subsystem, EventKind.MONITORING_HANDLE_EVENT_FAILED);

		this.monitor.handleEvent(event);
		verify(this.errorEventHandler, times(1)).handleGlobalError(event);
		verify(this.errorEventHandler, never()).handleError(any(), any());
		verify(subsystem, times(1)).stop();
	}

	@Test(description = "verifies if only the correct method of the error handler is called when a error occurs")
	public void handleError() {
		AbstractSubsystem subsystem = mock(AgvCoordinator.class);
		EventKind agvForkliftDamaged = EventKind.AGV_FORKLIFT_DAMAGED;
		FactoryEvent event = new FactoryEvent(subsystem, agvForkliftDamaged);

		this.monitor.handleEvent(event);
		verify(this.errorEventHandler, times(1)).handleError(subsystem, agvForkliftDamaged);
		verify(this.errorEventHandler, never()).handleGlobalError(event);
	}

	@Test(description = "verifies that the error handler is not used when the event is not of type error or global error")
	public void handleEventCheckThatErrorEventHandlerIsNotUsed() {
		AbstractSubsystem subsystem = mock(AgvCoordinator.class);
		FactoryEvent event = new FactoryEvent(subsystem, EventKind.CONVEYORS_LACK_OF_OIL);

		this.monitor.handleEvent(event);
		verifyZeroInteractions(this.errorEventHandler);
	}

}
