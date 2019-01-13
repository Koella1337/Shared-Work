package app.gui;

import javax.swing.JLabel;

public class SubsystemMenu extends GUIPanel{

	
	public SubsystemMenu(int fps, String title) {
		super(fps);
		
		this.add(new JLabel(title));
	}

}
