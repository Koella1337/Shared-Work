package app.timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

@SuppressWarnings("serial")
public class UpdateTimer extends Timer {

	public UpdateTimer(Updateable updateable, int updatesPerSecond) {
		super(getDelayInMillisForFps(updatesPerSecond), new UpdateListener(updateable));
	}

	private static final int getDelayInMillisForFps(int updatesPerSecond) {
		return (int) (1000.0 / (double) updatesPerSecond);
	}
}

class UpdateListener implements ActionListener {
	private final Updateable updateable;

	public UpdateListener(Updateable updateable) {
		this.updateable = updateable;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		updateable.update();
	}
}