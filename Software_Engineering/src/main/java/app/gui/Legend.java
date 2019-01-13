package app.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Legend extends GUIPanel{

	private List<LegendEntry> legend;
	
	protected Legend(int fps) {
		super(fps);
		this.legend = new ArrayList<>();
		this.legend.add(new LegendEntry(Color.red, "Storagesite"));
		this.legend.add(new LegendEntry(Color.green, "Forklift"));
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for(int i=0;i<legend.size();i++) {
			legend.get(i).draw(g, 4,4+i*22);
		}
	}
	
	private class LegendEntry {
		private Color color;
		private String text;
		
		public LegendEntry(Color color, String text) {
			super();
			this.color = color;
			this.text = text;
		}
		
		public void draw(Graphics g, int x, int y) {
			g.setColor(color);
			g.fillRect(x,y,20,20);
			g.setColor(Color.black);
			g.drawString(text,x+24,y+15);
		}
		
	}
	

	
	
}
