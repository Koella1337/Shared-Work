package factory.subsystems.warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

	public WarehouseSystem(MonitoringInterface monitor, Element xmlWarehouseElem) {
		super(monitor);
		Objects.requireNonNull(xmlWarehouseElem);
		
		NodeList storageSiteNodes = xmlWarehouseElem.getElementsByTagName("storagesite");
		for (int i = 0; i < storageSiteNodes.getLength(); i++) {
			storageSites.add(new StorageSite(this, i, (Element) storageSiteNodes.item(i)));
		}
	}

	@Override
	public SubsystemStatus getStatus() {
		return SubsystemStatus.RUNNING;		// TODO 
	}

	@Override
	public StorageSite receiveTask(WarehouseTask task) {
		StorageSite leastOverworkedSite = null;
		int leastOverworkedSiteTaskCount = -1;
		
		//choose Site that either accepts the Task or is the least overworked
		for (StorageSite s : storageSites) {
			int overworkedTaskCount = s.canAcceptTask(task);
			
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
		
		leastOverworkedSite.receiveTask(task);
		return leastOverworkedSite;
	}
	
	/** Called from a StorageSite when it completed a task. */
	public void taskCompleted(StorageSite source, WarehouseTask task) {
		this.notify(new FactoryEvent(this, EventKind.WAREHOUSE_TASK_COMPLETED, task, source.getOutputbox()));
	}
	
	@Override
	public int getContainerAmount(Material material) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> getTransactions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Placeable> getPlaceables() {
		return new ArrayList<Placeable>(storageSites); //TODO
	}

	@Override
	public String getName() {
		return "warehouse";
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
