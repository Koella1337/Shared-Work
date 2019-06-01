package factory.app.gui;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class SubsystemMenu extends GUIPanel{

	
	public SubsystemMenu(int fps, String title) {
		super(fps);
		
		this.add(new JLabel(title));
	}

}
