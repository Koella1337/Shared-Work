package factory.shared;

import java.awt.Graphics;

import factory.shared.Constants.PlaceableSize;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.enums.MaterialStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;

public class ResourceBox implements ContainerDemander, ContainerSupplier {

	private final AbstractSubsystem owningSystem;
	
	private final Position pos;
	private final MaterialStorageMap content = new MaterialStorageMap();
	
	public ResourceBox(AbstractSubsystem owningSystem, Position pos) {
		this.owningSystem = owningSystem;
		this.pos = pos;
		if (pos.xSize == 1 && pos.ySize == 1)
			Utils.assignSize(pos, PlaceableSize.RESOURCE_BOX);
	}
	
	/** returns all Materials stored in this box */
	public synchronized Material[] getStoredMaterials() {
		return content.getMap().keySet().toArray(new Material[0]);
	}
	
	/** @return the amount of stored containers in this ResourceBox <br> (across all Materials) */
	public synchronized int getContainerAmount() {
		int amount = 0;
		for (Material mat : content.getMap().keySet()) {
			amount += content.get(mat);
		}
		return amount;
	}
	
	public synchronized MaterialStatus getFullness() {
		int percent = (int) (((float) getContainerAmount() / (float) Constants.RESOURCE_BOX_MAX_CONTAINERS) * 100);
		
		if (percent == 0)
			return MaterialStatus.EMPTY;
		else if (percent < 15)
			return MaterialStatus.PERFECT;
		else if (percent < 30)
			return MaterialStatus.WELL;
		else if (percent < 50)
			return MaterialStatus.AVERAGE;
		else if (percent < 90)
			return MaterialStatus.BAD;
		else
			return MaterialStatus.TERRIBLE;
	}

	@Override
	public Position getPosition() {
		return this.pos;
	}

	@Override
	public synchronized void draw(Graphics g) {
		g.setColor(getFullness().uiColor);
		g.fillRect(1, 1, pos.xSize - 1, pos.ySize - 1);
		g.setColor(Constants.UI_BORDER_COLOR);	
		g.drawRect(0, 0, pos.xSize, pos.ySize);
		g.drawString(""+getContainerAmount(), pos.xSize/3, pos.ySize/2);
	}

	@Override
	public synchronized Container deliverContainer(Material material) {
		if (content.get(material) == null)
			throw new IllegalArgumentException("Could not deliver \"" + material + "\" since it isn't stored in this " + this.toString());
		return new Container(content.remove(material));
	}

	@Override
	public synchronized void receiveContainer(Container container) {
		content.add(container);
		
		if (getContainerAmount() == Constants.RESOURCE_BOX_MAX_CONTAINERS)
			owningSystem.notify(new FactoryEvent(owningSystem, EventKind.RESOURCEBOX_FULL, this));
		else if (getFullness() == MaterialStatus.TERRIBLE)
			owningSystem.notify(new FactoryEvent(owningSystem, EventKind.RESOURCEBOX_ALMOST_FULL, this));
	}
	
	@Override
	public String toString() {
		return "(ResourceBox at " + pos.toString() + ")";
	}
	
}
