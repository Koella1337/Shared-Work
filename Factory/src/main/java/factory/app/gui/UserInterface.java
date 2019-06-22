package factory.app.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import factory.shared.Constants;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;

class UserInterface implements Stoppable {

	private int fps;
	private MonitoringInterface monitor;

	private JFrame frame;
	private JPanel contentPane;

	private FactoryPanel factoryPanel;
	private MenuPanel menuPanel;
	private UIConfiguration config;
	
	private MonitorButton emergencyStop;
	private MonitorButton startButton;
	private MonitorButton errorFixedButton;

	public UserInterface(int fps, MonitoringInterface monitor, UIConfiguration config) {
		super();
		this.fps = fps;
		this.monitor = monitor;
		this.config = config;
		initUI();
	}

	@Override
	public void start() {
		this.frame.pack();
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);
	}

	@Override
	public void stop() {
		this.frame.dispose();
	}

	private void initUI() {
		this.frame = new JFrame("Toy Car Factory");
		this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.frame.setResizable(false);

		this.contentPane = (JPanel) frame.getContentPane();
		this.contentPane.setBackground(Color.LIGHT_GRAY);
		this.contentPane.setLayout(new BorderLayout());

		initFactoryPanel();
		initDefaultMenuPanel();
	}

	private void initFactoryPanel() {
		this.factoryPanel = new FactoryPanel(this.fps, this.monitor);
		this.factoryPanel.setPreferredSize(new Dimension(this.config.uiWidthFactory + 1, this.config.uiHeight + 1));
		this.contentPane.add(this.factoryPanel, BorderLayout.CENTER);
	}

	private void initDefaultMenuPanel() {
		this.menuPanel = new MenuPanel(this.fps, this.monitor);
		this.menuPanel.setBackground(Color.LIGHT_GRAY);
		this.menuPanel
				.setPreferredSize(new Dimension(new Dimension(Constants.UI_WIDTH_MENU, this.config.uiHeight + 1)));
		this.menuPanel.setLayout(null);

		Legend legend = new Legend(this.fps);
		legend.setBounds(20, 180, 360, 100);
		this.menuPanel.add(legend);

		startButton = new MonitorButton("START", m -> m.getStatus() == SubsystemStatus.STOPPED);
		startButton.setBounds(20,20,80,20);
		startButton.setFocusable(false);
		startButton.addActionListener(a -> {
			monitor.start();
		});
		this.menuPanel.add(startButton);
		
		emergencyStop = new MonitorButton("STOP", m -> m.getStatus() == SubsystemStatus.RUNNING);
		emergencyStop.addActionListener(a -> monitor.stop());
		emergencyStop.setBounds(120,20,80,20);
		emergencyStop.setFocusable(false);
		this.menuPanel.add(emergencyStop);
		
		errorFixedButton = new MonitorButton("error fixed", m -> m.getStatus() == SubsystemStatus.BROKEN);
		errorFixedButton.setBounds(220,20,120,20);
		errorFixedButton.addActionListener(a -> monitor.setStatus(SubsystemStatus.STOPPED));
		errorFixedButton.setFocusable(false);
		this.menuPanel.add(errorFixedButton);
		
		this.contentPane.add(menuPanel, BorderLayout.LINE_END);
	}

	public FactoryPanel getFactoryPanel() {
		return factoryPanel;
	}

	public void setFactoryPanel(FactoryPanel factoryPanel) {
		this.factoryPanel = factoryPanel;
	}

	public MenuPanel getMenuPanel() {
		return menuPanel;
	}

	public void setMenuPanel(MenuPanel menuPanel) {
		this.menuPanel = menuPanel;
	}

	@SuppressWarnings("serial")
	class MonitorButton extends JButton {
		private Predicate<MonitoringInterface> enableCondition;

		public MonitorButton(String label, Predicate<MonitoringInterface> enableCondition) {
			super(label);
			this.enableCondition = enableCondition;
		}

		@Override
		public boolean isEnabled() {
			if (enableCondition == null || UserInterface.this.monitor == null)
				return false;
			return enableCondition.test(UserInterface.this.monitor);
		}
	}
}
