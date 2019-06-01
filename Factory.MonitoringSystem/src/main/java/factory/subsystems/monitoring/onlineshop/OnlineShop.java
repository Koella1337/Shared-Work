package factory.subsystems.monitoring.onlineshop;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import factory.shared.enums.Material;
import factory.shared.interfaces.Stoppable;
import factory.subsystems.monitoring.InvalidOrderException;
import factory.subsystems.monitoring.MonitoringSystem;

public class OnlineShop implements Stoppable {

	private MonitoringSystem monitor;
	private JFrame frame;
	private JPanel contentPane;
	private JTextField username;
	private JPasswordField password;
	private JSpinner amount;
	private JComboBox<Material> color;
	private JButton submitOffer;

	public OnlineShop(MonitoringSystem monitor) {
		super();
		this.monitor = monitor;
		this.frame = new JFrame("Car Shop");
		this.frame.setPreferredSize(new Dimension(320, 180));
		this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.frame.setResizable(false);
		this.contentPane = (JPanel) frame.getContentPane();
		this.contentPane.setLayout(null);
		initUI();
	}

	private void initUI() {
		JLabel userNameLabel = new JLabel("username (test)");
		userNameLabel.setBounds(10, 10, 140, 20);
		this.contentPane.add(userNameLabel);

		username = new JTextField();
		username.setBounds(10, 30, 140, 20);
		username.setEditable(true);
		this.contentPane.add(username);

		JLabel passwordLabel = new JLabel("password (1234)");
		passwordLabel.setBounds(10, 50, 140, 20);
		this.contentPane.add(passwordLabel);

		password = new JPasswordField();
		password.setBounds(10, 70, 140, 20);
		password.setEditable(true);
		this.contentPane.add(password);

		JLabel amountLabel = new JLabel("amount");
		amountLabel.setBounds(160, 10, 140, 20);
		this.contentPane.add(amountLabel);

		SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(1, 1, 1000, 1);
		amount = new JSpinner(spinnerNumberModel);
		amount.setBounds(160, 30, 140, 20);
		this.contentPane.add(amount);

		JLabel colorLabel = new JLabel("color");
		colorLabel.setBounds(160, 50, 100, 20);
		this.contentPane.add(colorLabel);

		color = new JComboBox<Material>(new Material[] { Material.COLOR_BLACK,Material.COLOR_BLUE, Material.COLOR_GRAY, Material.COLOR_GREEN, Material.COLOR_RED, Material.COLOR_WHITE });
		color.setBounds(160, 70, 140, 18);
		this.contentPane.add(color);

		submitOffer = new JButton("submit");
		submitOffer.addActionListener(a -> submitOffer());
		submitOffer.setBounds(160, 105, 140, 24);
		this.contentPane.add(submitOffer);
	}

	private void submitOffer() {
		@SuppressWarnings("deprecation")
		OnlineShopUser user = new OnlineShopUser(username.getText(), password.getText());
		Order order = new Order(user, (int) amount.getValue(), (Material) color.getSelectedItem());
		
		try {
			monitor.addOrder(order);
			JOptionPane.showMessageDialog(this.frame,"order submitted");
		} catch (InvalidOrderException e) {
			JOptionPane.showMessageDialog(this.frame,e.getMessage());
		}
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

}
