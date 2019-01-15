package factory.subsystems.warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import database.Database;
import factory.shared.AbstractSubsystem;
import factory.shared.FactoryEvent;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Placeable;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.warehouse.interfaces.WarehouseMonitorInterface;

public class WarehouseSystem extends AbstractSubsystem implements WarehouseMonitorInterface {
	
	private final List<StorageSite> storageSites = new ArrayList<>();
	private final List<Placeable> placeables = new ArrayList<>();
	
	private SubsystemStatus status;
	private boolean wasSubsystemAlreadyStarted;

	public WarehouseSystem(MonitoringInterface monitor, Element xmlWarehouseElem) {
		super(monitor);
		Objects.requireNonNull(xmlWarehouseElem);
		
		NodeList storageSiteNodes = xmlWarehouseElem.getElementsByTagName("storagesite");
		for (int i = 0; i < storageSiteNodes.getLength(); i++) {
			StorageSite newSite = new StorageSite(this, i, (Element) storageSiteNodes.item(i));
			storageSites.add(newSite);
			placeables.add(newSite);
			placeables.addAll(newSite.getPlaceables());
		}
		
		this.status = SubsystemStatus.WAITING;
		this.wasSubsystemAlreadyStarted = false;
		
		Database.INSTANCE.initialize();
	}

	@Override
	public SubsystemStatus getStatus() {
		return status;
	}
	
	@Override
	public StorageSite receiveTask(WarehouseTask task) {
		StorageSite leastOverworkedSite = null;
		int leastOverworkedSiteTaskCount = Integer.MAX_VALUE;
		
		//choose Site that either accepts the Task or is the least overworked
		for (StorageSite s : storageSites) {
			int overworkedTaskCount = s.canAcceptTask(task);
			if (overworkedTaskCount == -1)
				continue;
			
			if (overworkedTaskCount == 0) {
				s.receiveTask(task);
				return s;
			}
			else {
				if (leastOverworkedSite == null || overworkedTaskCount < leastOverworkedSiteTaskCount) {
					leastOverworkedSite = s;
					leastOverworkedSiteTaskCount = overworkedTaskCount;
				}
			}
		}
		
		if (leastOverworkedSite == null)
			throw new IllegalArgumentException("The task \"" + task + "\" can not be accepted by any StorageSite.");
		
		leastOverworkedSite.receiveTask(task);
		return leastOverworkedSite;
	}
	
	/** Called from a StorageSite when it completed a task. */
	public void taskCompleted(StorageSite source, WarehouseTask task) {
		this.notify(new FactoryEvent(this, EventKind.WAREHOUSE_TASK_COMPLETED, task, source.getOutputbox()));
	}
	
	@Override
	public int getContainerAmount(Material material) {
		int containerAmount = 0;
		for (StorageSite s : storageSites) {
			containerAmount += s.getContainerAmount(material);
		}
		return containerAmount;
	}

	@Override
	public List<String> getTransactions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Placeable> getPlaceables() {
		return placeables;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void start() {
		if (!wasSubsystemAlreadyStarted) {
			for (StorageSite s : storageSites) {
				Thread thread = new Thread(() -> s.start());
				thread.setDaemon(true);
				thread.start();
			}
			wasSubsystemAlreadyStarted = true;
		}
		status = SubsystemStatus.RUNNING;
	}

	@Override
	public void stop() {
		status = SubsystemStatus.STOPPED;
	}

}
