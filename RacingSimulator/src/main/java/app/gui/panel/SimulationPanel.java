package app.gui.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import app.gui.CarGui;
import app.gui.GuiHandler;
import app.model.car.Car;

@SuppressWarnings("serial")
public class SimulationPanel extends JPanel {
	private List<? extends Car> carList;
	private Car selectedCar;

	public SimulationPanel(GuiHandler guiHandler) {
		super();
		this.setBackground(Color.WHITE);
		this.addMouseMotionListener(new CustomMouseAdapter(guiHandler));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (getCarList() != null && !getCarList().isEmpty()) {
			this.getCarList().stream()
				.filter(car -> !isSelectedCarAndStillValid(car))
				.map(car -> new CarGui(car))
				.forEach(carGui -> carGui.drawUnselectedCar(g));
			
			if(this.selectedCar != null && isSelectedCarAndStillValid(selectedCar)) {
				CarGui selectedGui = new CarGui(selectedCar);
				selectedGui.drawSelectedCar(g);
			}
		}
	}

	private boolean isSelectedCarAndStillValid(Car car) {
		return   selectedCar != null && car.equals(selectedCar) &&  getCarList().contains(selectedCar) ;
	}

	public List<? extends Car> getCarList() {
		return carList;
	}

	public void setCarList(List<? extends Car> carList) {
		this.carList = carList;
	}

	public void setSelectedCar(Car car) {
		this.selectedCar = car;
	}
}