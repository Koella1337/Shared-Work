package app.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

class UpdateTimer extends Timer {

	public UpdateTimer(final JPanel panelToUpdate, int fps) {
		super(getDelayInMillisForFps(fps), new JPanelUpdateActionListener(panelToUpdate));
	}

	private static final int getDelayInMillisForFps(int fps) {
		return (int) (1000.0 / (double) fps);
	}

}

class JPanelUpdateActionListener implements ActionListener {
	private final JPanel panelToUpdate;

	public JPanelUpdateActionListener(JPanel panel) {
		panelToUpdate = panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		panelToUpdate.repaint();
	}
}