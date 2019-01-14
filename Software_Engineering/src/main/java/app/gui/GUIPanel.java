package app.gui;

import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GUIPanel extends JPanel {

	protected int frameIndex = 0;// only used for debugging //TODO @thomas remove

	protected GUIPanel(int fps) {
		Timer timer = new UpdateTimer(this, fps);
		timer.setInitialDelay(0);
		timer.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		addDebuggingInformation(g);
	}

	/**
	 * TODO remove
	 */
	private void addDebuggingInformation(Graphics g) {
		g.drawString("frameIndex: " + frameIndex++, getWidth() - 100, getHeight() - 150);
		g.drawString(getClass().getSimpleName(), getWidth() - 100, getHeight() - 130);
		g.drawString(getWidth() + " / " + getHeight(), getWidth() - 100, getHeight() - 110);
	}

}
