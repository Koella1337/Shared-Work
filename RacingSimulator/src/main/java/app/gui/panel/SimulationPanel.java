package app.gui.panel;

import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import app.gui.CarGui;
import app.gui.GuiHandler;
import app.model.car.Car;

@SuppressWarnings("serial")
public class SimulationPanel extends JPanel {
	private List<? extends Car> carList;

	public SimulationPanel(GuiHandler guiHandler) {
		super();
		this.addMouseListener(new CustomMouseAdapter(guiHandler));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (getCarList() != null && !getCarList().isEmpty()) {
			this.getCarList().stream().map(car -> new CarGui(car)).forEach(carGui -> carGui.draw(g));
		}
	}

	public List<? extends Car> getCarList() {
		return carList;
	}

	public void setCarList(List<? extends Car> carList) {
		this.carList = carList;
	}
}