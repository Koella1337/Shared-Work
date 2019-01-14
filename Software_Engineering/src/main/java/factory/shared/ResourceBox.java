package factory.shared;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import factory.shared.enums.Material;
import factory.shared.interfaces.ContainerDemander;
import factory.shared.interfaces.ContainerSupplier;

public class ResourceBox implements ContainerDemander, ContainerSupplier {

	private final Position pos;
	private final Map<Material, Integer> materialToAmountMap = new HashMap<>();
	
	public ResourceBox(Position pos) {
		this.pos = pos;
	}

//	@Override
//	public void deliverContainer(Container container) {
//		Material material = container.getMaterial();
//		int currentAmount = materialToAmountMap.get(material);
//		int newAmount = currentAmount - container.getAmount();
//		materialToAmountMap.put(material, newAmount);
//	}

//	@Override
//	public void receiveContainer(Container container) {
//		Material material = container.getMaterial();
//		int currentAmount = materialToAmountMap.get(material);
//		int newAmount = currentAmount + container.getAmount();
//		materialToAmountMap.put(material, newAmount);
//	}

	public Map<Material, Integer> getMaterialToAmountMap() {
		return materialToAmountMap;
	}

	@Override
	public Position getPosition() {
		return this.pos;
	}

	@Override
	public void draw(Graphics g) {
		//TODO
		g.setColor(Color.DARK_GRAY);	
		g.drawRect(0, 0, this.pos.xSize, this.pos.ySize);
		g.drawString("RES", 0, 15);	//TODO remove
	}

	@Override
	public Container deliverContainer(Material material) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void receiveContainer(Container container) {
		// TODO Auto-generated method stub
		
	}
	
}
