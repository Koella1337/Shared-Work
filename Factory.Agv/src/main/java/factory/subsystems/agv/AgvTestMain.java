package factory.subsystems.agv;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import factory.shared.Position;

public class AgvTestMain{

    private static JFrame frame;
    private static DrawPanel dp;
    private static Image forkliftImage;
    private static Forklift f;
    
	public static void main(String[] args) {
		AgvCoordinator m = new AgvCoordinator(null, null, null); // we won't contact the monitor in this test
		f = new Forklift(new Position(50,50), m);
		m.addForklift(f);
		
		List<Position> path = new LinkedList<>();
		path.add(new Position(500,50));
		path.add(new Position(50,500));
		path.add(new Position(250,250));
		path.add(new Position(500,500));
		
		f.setPath(path);
		
		forkliftImage = new ImageIcon("resources/Forklift-v2.png").getImage();
		SwingUtilities.invokeLater(() ->
		{
			frame = createFrame();
			frame.pack();
			frame.setSize(1000, 800);
			frame.setLocation(200, 200);
			frame.setVisible(true);
		}
		
				);
		while(true)
		{
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dp.repaint();
		}
	}
	
	private static JFrame createFrame()
	{
		JFrame newFrame = new JFrame();
		newFrame.setTitle("AGV Test Demonstration");        
        newFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		dp = new DrawPanel();
		newFrame.getContentPane().add(dp);
		return newFrame;
	}
	
	@SuppressWarnings("serial")
	private static class DrawPanel extends JPanel
	{
		private void doDrawing(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.translate( (int)Math.round(f.getPosition().xPos), (int)Math.round(f.getPosition().yPos));
			Position vec = f.getVec();
			Double angle = Math.PI - Math.atan2(vec.xPos, vec.yPos);
			System.out.println(angle);
			g2.rotate(angle);
			g.drawImage(forkliftImage, -60, -60, 120, 120, null);
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			doDrawing(g);
		}
	}

}
