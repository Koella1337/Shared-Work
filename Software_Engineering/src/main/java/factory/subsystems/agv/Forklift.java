package factory.subsystems.agv;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;

import factory.shared.Constants;
import factory.shared.Container;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.interfaces.Placeable;

public class Forklift implements Placeable {
	private static final double SPEED = 10000000; // distance moved per nanosecond (inverted for easier calculation)
	private static final double THRESHOLD = 0.001; // maximum distance to a target to have reached it
	private long lastTime; // last time the forklift's position was updated
	private boolean shutdown;

	private List<Position> path;
	private Position vec = new Position(-1, 0); // this is for drawing, so it mustn't be null even at the start

    private static Image forkliftImage;
    
    private AgvCoordinator coordinator;

	public Position getVec() {
		return vec;
	}

	private Position pos;
	
	private Container carriedBox;

	private AgvTask currentTask;

	public Forklift(Position pos, AgvCoordinator coordinator) {
		shutdown = false;
		this.pos = pos;
		carriedBox = null;
		this.coordinator = coordinator;

		lastTime = System.nanoTime();
		Timer scheduler = new Timer();
		scheduler.scheduleAtFixedRate(move, 500l, 500l); // update every half-second
		
		forkliftImage = new ImageIcon("resources/Forklift-v3.png").getImage();
	}


	public AgvTask getCurrentTask() {
		return currentTask;
	}

	public void assignTask(AgvTask newTask) {
		if (currentTask == null) {
			// TODO: warn about unfinished task
			// actually, I'm making the coordinator too,
			// so I just won't assign to busy forklifts, who cares
			// the monitor doesn't have to know
			currentTask = newTask;
		}
		else
		{
		}
	}

	public Position getPosition() {
		return pos;
	}

	public void setPath(List<Position> newPath) {
		this.path = newPath;
	}

	public void shutdown() {
		shutdown = true;
	}

	public void resume() {
		shutdown = false;
	}
	
	private boolean targetReached(Position target)
	{
		return Position.length(Position.subtractPosition(pos, target)) < THRESHOLD;
	}

	final TimerTask move = new TimerTask() {
		// this is periodically called to update the forklift's position
		public void run() {
			long newTime = System.nanoTime();
			long timeElapsed = newTime - lastTime;
			lastTime = newTime;
			if (shutdown) {
				return;
			}
			if (path != null && !path.isEmpty()) // no target means no moving
			{
				// Vector to next target along path
				// this is saved in a field because it'll also be used for the direction of the
				// graphic
				vec = Position.subtractPosition(path.get(0),pos);
				// length of vector
				Double len = Position.length(vec);
				// distance we can move
				Double moveLen = timeElapsed / SPEED;
				// don't overshoot the target
				moveLen = Math.min(moveLen, len);
				// Normalise the vector and set it to the appropriate length
				vec = Position.divide(Position.multiply(vec, (int)Math.round(moveLen)), (int)Math.round(len));
				// add it to our position to obtain the new one
				pos = Position.addPosition(pos,vec);

				// when we reach a target, remove it
				// due to floating point numbers we use a small range
				if (targetReached(path.get(0))) {
					path.remove(0);
				}
				if (currentTask != null) {
					if (targetReached(currentTask.getPickup().getPosition())) {
						carriedBox = currentTask.getPickup().deliverContainer(currentTask.getMaterial());
					}
					if (targetReached(currentTask.getDropoff().getPosition())) {
						currentTask.getDropoff().receiveContainer(carriedBox);
						carriedBox = null;
						currentTask = null;
						
					}
				}
			}
		}
	};

	@Override
	public void draw(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			//g2.translate( (int)Math.round(getPosition().xPos), (int)Math.round(getPosition().yPos));
			Position vec = getVec();
			Double angle = Math.PI - Math.atan2(vec.xPos, vec.yPos);
			g2.rotate(angle);
			int sizeX = Constants.PlaceableSize.FORKLIFT.x;
			int sizeY = Constants.PlaceableSize.FORKLIFT.y;
			g.drawImage(forkliftImage, -sizeX/2, -sizeY/2, sizeX, sizeY, null);
			g2.rotate(-angle);
	}
}
