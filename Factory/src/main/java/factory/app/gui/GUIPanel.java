package factory.app.gui;

import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GUIPanel extends JPanel {


	protected GUIPanel(int fps) {
		Timer timer = new UpdateTimer(this, fps);
		timer.setInitialDelay(0);
		timer.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

}
