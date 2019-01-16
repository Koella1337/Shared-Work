package app.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Predicate;

import javax.swing.JButton;

import factory.shared.enums.SubsystemStatus;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

@SuppressWarnings("serial")
class MenuBarPanel extends GUIPanel {

	private MonitoringInterface monitor;
	private MonitorButton emergencyStop;
	private MonitorButton startButton;
	private MonitorButton errorFixedButton;

	public MenuBarPanel(int fps, MonitoringInterface monitor) {
		super(fps);
		this.monitor = monitor;
		this.setBackground(Color.LIGHT_GRAY);

		emergencyStop = new MonitorButton("STOP", m -> m.getStatus() == SubsystemStatus.RUNNING);
		emergencyStop.addActionListener(a -> monitor.stop());
		this.add(emergencyStop);

		startButton = new MonitorButton("START", m -> m.getStatus() == SubsystemStatus.STOPPED);
		startButton.addActionListener(a -> {
			monitor.start();
		});
		this.add(startButton);
		
		errorFixedButton = new MonitorButton("error fixed", m -> m.getStatus() == SubsystemStatus.BROKEN);
		errorFixedButton.addActionListener(a -> monitor.setStatus(SubsystemStatus.STOPPED));
		this.add(errorFixedButton);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	class MonitorButton extends JButton {
		private Predicate<MonitoringInterface> enableCondition;

		public MonitorButton(String label, Predicate<MonitoringInterface> enableCondition) {
			super(label);
			this.enableCondition = enableCondition;
		}

		@Override
		public boolean isEnabled() {
			if (enableCondition == null || MenuBarPanel.this.monitor == null) 
				return false;
			return enableCondition.test(MenuBarPanel.this.monitor);
		}
	}

}
