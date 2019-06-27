package app.gui.panel;

import javax.swing.JPanel;

public class CarStatsPanel extends JPanel {

	
	
	
//	private PointPanel speedPanel;
//
//	public CarStatsPanel() {
//		super();
//		this.setPreferredSize(new Dimension(getWidth(), 700));
//		this.setBackground(Color.LIGHT_GRAY);
//		this.speedPanel = new PointPanel();
//		this.add(speedPanel);
//	}
//
//	public void showCarStats(Car car) {
//		speedPanel.label = "car";
//		speedPanel.points = 4;
//		speedPanel.maxPoints = 4;
//	}
//
//	class PointPanel extends JComponent {
//		private String label;
//		private int points;
//		private int maxPoints;
//
//		public PointPanel() {
//			setBackground(Color.green);
//			this.setPreferredSize(new Dimension(100, 100));
//		}
//
//		@Override
//		public void paintComponent(Graphics g) {
//			super.paintComponent(g);
//			if(this.label != null) {
//				g.setColor(Color.red);
//				g.drawString(label, 0, 0);
//				for (int i = 0; i < maxPoints; i++) {
//					g.drawOval(i * 10, 10, 6, 6);
//				}
//			}
//			
//		}
//
//	}
}