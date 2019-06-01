package factory.subsystems.assemblyline;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import factory.shared.AbstractSubsystem;
import factory.shared.ResourceBox;
import factory.shared.enums.Material;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Monitor;
import factory.shared.interfaces.Placeable;
import factory.subsystems.assemblyline.interfaces.AssemblyLineSystemInterface;

public class AssemblyLineSystem extends AbstractSubsystem implements AssemblyLineSystemInterface {
	
	private SubsystemStatus status;
	private boolean wasSubsystemAlreadyStarted;

	private final List<AssemblyLine> assemblyLines = new ArrayList<>();
	private final List<Placeable> placeables = new ArrayList<>();
	
	public AssemblyLineSystem(Monitor monitor, Element xmlAssemblyLinesElem) {
		super(monitor, AssemblyLineSystem.class.getSimpleName());
		Objects.requireNonNull(xmlAssemblyLinesElem);
		
		//xml init
		NodeList assemblyLineNodes = xmlAssemblyLinesElem.getElementsByTagName("assemblyline");
		for (int assemblyLineID = 0; assemblyLineID < assemblyLineNodes.getLength(); assemblyLineID++) {
			AssemblyLine newSite = new AssemblyLine(
					assemblyLineID, 
					this, 
					determineAssemblyLineColor(assemblyLineID), 
					(Element) assemblyLineNodes.item(assemblyLineID)
			);
			assemblyLines.add(newSite);
			placeables.add(newSite);
			placeables.addAll(newSite.getPlaceables());
		}
		
		//general init
		this.status = SubsystemStatus.WAITING;
		this.wasSubsystemAlreadyStarted = false;
	}
	
	/**
	 * Determines the color of the cars that the AssemblyLine will produce.<br>
	 * This is based on it's ID, which is taken modulo 6 and then assigned a color.
	 * 
	 * @param assemblyLineID - the AssemblyLine's id.
	 * @return the color the AssermblyLine should produce.
	 */
	private Material determineAssemblyLineColor(int assemblyLineID) {
		switch (assemblyLineID % 6) {
			case 0: return Material.COLOR_BLACK;
			case 1: return Material.COLOR_GRAY;
			case 2: return Material.COLOR_RED;
			case 3: return Material.COLOR_GREEN;
			case 4: return Material.COLOR_BLUE;
			case 5: return Material.COLOR_WHITE;
			default: //non reachable if assemblyLineID >= 0
				return Material.COLOR_BLACK;
		}
	}

	@Override
	public void addCustomOrder(Material color, int quantity) {
		assemblyLines.stream()
			.filter(assemblyLine -> assemblyLine.getColor() != color)
			.forEach(assemblyLine -> assemblyLine.produceDifferentColoredCars(color, quantity));
	}
	
	@Override
	public void start() {
		if (!wasSubsystemAlreadyStarted) {
			for(AssemblyLine line : assemblyLines) {
				new Thread(() -> line.start()).start();
			}
			wasSubsystemAlreadyStarted = true;
		}
		status = SubsystemStatus.RUNNING;
	}
	
	@Override
	public void stop() {
		status = SubsystemStatus.STOPPED;
	}
	
	@Override
	public void stopProduction(Material color) {
		assemblyLines.stream()
			.filter(assemblyLine -> assemblyLine.getColor() == color);
			//TODO: implement
	}

	@Override
	public SubsystemStatus getStatus() {
		return status;
	}

	@Override
	public List<Placeable> getPlaceables() {
		return placeables;
	}

	public List<Placeable> getAgvAccessiblePlaceables() {
		List<Placeable> accessiblePlaceables = new LinkedList<>();
		for(Placeable p : getPlaceables())
		{
			if(p instanceof ResourceBox || p instanceof Robot)
			{
				accessiblePlaceables.add(p);
			}
		}
		return accessiblePlaceables;
	}

}