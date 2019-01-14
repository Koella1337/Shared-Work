package app.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import factory.shared.AbstractSubsystem;
import factory.shared.Constants;
import factory.shared.FactoryEvent;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.monitoring.onlineshop.OnlineShopUser;
import factory.subsystems.monitoring.onlineshop.Order;

class UserInterface implements Stoppable {

	private int fps;
	private MonitoringInterface monitor;

	private JFrame frame;
	private JPanel contentPane;

	private FactoryPanel factoryPanel;
	private MenuPanel menuPanel;
	private UIConfiguration config;

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
		// TODO Auto-generated method stub
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
		this.factoryPanel.setPreferredSize(new Dimension(this.config.uiWidthFactory+1,this.config.uiHeight+1));
		this.contentPane.add(this.factoryPanel, BorderLayout.CENTER);
	}

	private void initDefaultMenuPanel() {
		this.menuPanel = new MenuPanel(this.fps, this.monitor);
		this.menuPanel.setBackground(Color.LIGHT_GRAY);
		this.menuPanel.setPreferredSize(new Dimension(new Dimension(Constants.UI_WIDTH_MENU, this.config.uiHeight+1)));
		this.menuPanel.setLayout(null);

		JButton addOrderButton = new JButton("place order");
		addOrderButton.addActionListener(a -> monitor.addOrder(new Order(new OnlineShopUser("thomas"), 3)));
		addOrderButton.setBounds(20,100,160,20);
		this.menuPanel.add(addOrderButton);

		JButton carFinishedButton = new JButton("car finished");
		carFinishedButton.addActionListener(a -> monitor.handleEvent(new FactoryEvent(monitor.getAssemblyLine(), EventKind.CAR_FINISHED, Material.CAR)));
		carFinishedButton.setBounds(200,100,160,20);
		this.menuPanel.add(carFinishedButton);
		
		Legend legend = new Legend(30);
		legend.setBounds(20,180,360,100);
		this.menuPanel.add(legend);
		
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

	public void setCurrentSubsystem(AbstractSubsystem subsystem) {
		this.menuPanel.setCurrentSubSystem(subsystem);
	}



}
