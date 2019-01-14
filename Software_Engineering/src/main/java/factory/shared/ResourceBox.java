package factory.shared;

import java.awt.Graphics;

import factory.shared.enums.Material;
import factory.shared.enums.MaterialStatus;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;

public class ResourceBox implements ContainerDemander, ContainerSupplier {

	private final Position pos;
	private final MaterialStorageMap content = new MaterialStorageMap();
	
	public ResourceBox(Position pos) {
		this.pos = pos;
		for (int i = 0; i < 40; i++)	//TODO: remove !!
			this.receiveContainer(new Container(Material.CAR_BODIES));
	}
	
	/** @return the amount of stored containers in this ResourceBox <br> (across all Materials) */
	public int getStoredContainerAmount() {
		int amount = 0;
		for (Material mat : content.getMap().keySet()) {
			amount += content.get(mat);
		}
		return amount;
	}
	
	public MaterialStatus getFullness() {
		int percent = (int) (((float) getStoredContainerAmount() / (float) Constants.RESOURCE_BOX_MAX_CONTAINERS) * 100);
		
		if (percent == 0)
			return MaterialStatus.EMPTY;
		else if (percent < 10)
			return MaterialStatus.PERFECT;
		else if (percent < 25)
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
	public void draw(Graphics g) {
		g.setColor(getFullness().uiColor);
		g.fillRect(1, 1, pos.xSize - 1, pos.ySize - 1);
		g.setColor(Constants.UI_BORDER_COLOR);	
		g.drawRect(0, 0, pos.xSize, pos.ySize);
		g.drawString(""+getStoredContainerAmount(), pos.xSize/3, pos.ySize/2);
	}

	@Override
	public Container deliverContainer(Material material) {
		if (content.get(material) == null)
			throw new IllegalArgumentException("Could not deliver \"" + material + "\" since it isn't stored in this " + this.toString());
		return new Container(content.remove(material));
	}

	@Override
	public void receiveContainer(Container container) {
		if (getStoredContainerAmount() == Constants.RESOURCE_BOX_MAX_CONTAINERS)
			throw new IllegalArgumentException(this.toString() + " is already full!");
		content.add(container);
	}
	
	@Override
	public String toString() {
		return "ResourceBox at " + pos.toString();
	}
	
}
