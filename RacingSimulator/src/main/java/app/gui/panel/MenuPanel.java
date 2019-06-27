package app.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import app.gui.GuiHandler;
import app.model.car.Car;

@SuppressWarnings("serial")
public class MenuPanel extends JPanel {
	private GuiHandler guiHandler;
	private CarStatsPanel carStatsPanel;

	public MenuPanel(GuiHandler guiHandler) {
		this.guiHandler = guiHandler;

		this.setBackground(Color.WHITE);

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setPreferredSize(new Dimension(300, this.getHeight()));

		JPanel buttonPanel = new JPanel();
		this.add(buttonPanel);

		GridBagLayout gridBagLayout = new GridBagLayout();
		buttonPanel.setLayout(gridBagLayout);

		var startButton = new JButton("start Simulation");
		startButton.addActionListener(a -> guiHandler.startSimulation());

		var pauseSimulation = new JButton("pause Simulation");// TODO
		pauseSimulation.addActionListener(a -> guiHandler.pauseSimulation());

		var resetSimulation = new JButton("reset Simulation");// TODO
		resetSimulation.addActionListener(a -> guiHandler.resetSimulation());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipadx = 20;
		gbc.ipady = 20;

		buttonPanel.add(startButton, gbc);
		gbc.gridy++;

		buttonPanel.add(pauseSimulation, gbc);
		gbc.gridy++;

		buttonPanel.add(resetSimulation, gbc);
		gbc.gridy++;

		this.carStatsPanel= new CarStatsPanel();
		this.add(carStatsPanel);
	}
	
	public void showCarStats(Car car) {
		this.carStatsPanel.showCarStats(car);
	}
}