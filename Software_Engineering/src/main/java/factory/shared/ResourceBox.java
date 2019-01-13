package factory.shared;

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
		g.drawString("Res.Box", this.pos.xPos+2, this.pos.yPos);
		g.drawRect(this.pos.xPos, this.pos.yPos, 20,20);
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
