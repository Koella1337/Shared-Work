package factory.app.gui;

import java.awt.Graphics;

import javax.swing.JTextArea;

import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.monitoring.onlineshop.Order;

@SuppressWarnings("serial")
public class OrderListView extends GUIPanel {

	private MonitoringInterface monitor;

	private JTextArea orderList;

	protected OrderListView(int fps, MonitoringInterface monitor) {
		super(fps);
		this.monitor = monitor;
		this.setLayout(null);
		this.orderList = new JTextArea();
		this.orderList.setBounds(0,0,360,200);
		this.add(orderList);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		StringBuilder orders = new StringBuilder();
		this.monitor.getOrderMap().forEach((user, orderlist) -> {
			orders.append(user.getName() + "\n");
			for (Order order : orderlist) {
				orders.append("   " + order.getColor() + " " + order.getAmount() + "\n");
			}
		});

		this.orderList.setText("ORDERS:\n"+orders.toString());
	}

}
