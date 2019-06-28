package app.gui.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Set;

import javax.swing.JPanel;

import app.gui.CarGui;
import app.gui.GuiHandler;
import app.model.car.Car;

@SuppressWarnings("serial")
public class SimulationPanel extends JPanel {
	private Set<? extends Car> carList;
	private Car selectedCar;

	public SimulationPanel(GuiHandler guiHandler) {
		super();
		this.setBackground(Color.WHITE);
		CustomMouseAdapter customMouseAdapter = new CustomMouseAdapter(guiHandler);
		this.addMouseMotionListener(customMouseAdapter);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (getCarList() != null && !getCarList().isEmpty()) {
			this.getCarList().stream().filter(car -> !isSelectedCarAndStillValid(car)).map(car -> new CarGui(car))
					.forEach(carGui -> carGui.draw(g));
			
			if(this.selectedCar != null && isSelectedCarAndStillValid(selectedCar)) {
				CarGui selectedGui = new CarGui(selectedCar);
				selectedGui.drawSelected(g);
			}
		}
	}

	private boolean isSelectedCarAndStillValid(Car car) {
		return   selectedCar != null && car.equals(selectedCar) &&  getCarList().contains(selectedCar) ;
	}

	public Set<? extends Car> getCarList() {
		return carList;
	}

	public void setCarList(Set<? extends Car> carList) {
		this.carList = carList;
	}

	public void setSelectedCar(Car car) {
		this.selectedCar = car;
	}
}