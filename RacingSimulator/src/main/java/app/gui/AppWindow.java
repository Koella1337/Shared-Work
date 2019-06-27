package app.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AppWindow extends JFrame {

	private JPanel contentPane;
	private JPanel simulationPanel;
	private JPanel menuPanel;
	
	private int trackWidth;
	private int trackHeight;

	public AppWindow(int trackWidth, int trackHeight) {
		this.trackWidth = trackWidth;
		this.trackHeight = trackHeight;
		
		this.contentPane = (JPanel) getContentPane();
		
		this.simulationPanel = new JPanel();
		this.simulationPanel.setSize(trackWidth, trackHeight);
		

		
		
	}

	
	
}
