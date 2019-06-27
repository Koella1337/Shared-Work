package app.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import app.gui.panel.MenuPanel;
import app.gui.panel.SimulationPanel;
import app.model.car.Car;

@SuppressWarnings("serial")
public class AppWindow extends JFrame {

	private GuiHandler guiHandler;
	private JPanel contentPane;
	private SimulationPanel simulationPanel;
	private MenuPanel menuPanel;

	public AppWindow(GuiHandler guiHandler, int trackWidth, int trackHeight) {
		this.guiHandler = guiHandler;
		this.setLayout(new BorderLayout());
		this.contentPane = (JPanel) getContentPane();

		initSimulationPanel(trackWidth, trackHeight);
		initMenuPanel();
		displayWindow();
	}

	private void displayWindow() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.setResizable(false);
	}

	private void initMenuPanel() {
		this.menuPanel = new MenuPanel(this.guiHandler);
		this.contentPane.add(menuPanel, BorderLayout.EAST);
	}

	private void initSimulationPanel(int trackWidth, int trackHeight) {
		this.simulationPanel = new SimulationPanel(guiHandler);
		this.simulationPanel.setPreferredSize(new Dimension(trackWidth, trackHeight));
		this.contentPane.add(simulationPanel, BorderLayout.CENTER);
	}

	public void setCars(List<? extends Car> carList) {
		this.simulationPanel.setCarList(carList);
	}

	public SimulationPanel getSimulationPanel() {
		return simulationPanel;
	}

	public MenuPanel getMenuPanel() {
		return menuPanel;
	}

	

	
}
