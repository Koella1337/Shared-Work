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
		CustomMouseAdapter customMouseAdapter = new CustomMouseAdapter(guiHandler);
		this.addMouseMotionListener(customMouseAdapter);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (getCarList() != null && !getCarList().isEmpty()) {
			this.getCarList().stream().filter(car -> !isSelected(car)).map(car -> new CarGui(car))
					.forEach(carGui -> carGui.draw(g));
			
			if(this.selectedCar != null) {
				CarGui selectedGui = new CarGui(selectedCar);
				selectedGui.drawSelected(g);
			}
		}
	}

	private boolean isSelected(Car car) {
		return selectedCar != null && car.equals(selectedCar);
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