package factory.subsystems.agv;

import java.awt.Graphics;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.interfaces.Placeable;

public class Forklift implements Placeable {
	private static final double SPEED = 10000000; // distance moved per nanosecond (inverted for easier calculation)
	private static final double THRESHOLD = 0.001; // maximum distance to a target to have reached it
	private long lastTime; // last time the forklift's position was updated
	private boolean shutdown;

	private List<Position> path;
	private Position vec;

	public Position getVec() {
		return vec;
	}

	private Position pos;
	private ResourceBox carriedBox;

	private Graphics graph;

	private AgvTask currentTask;

	public Forklift(Position pos) {
		shutdown = false;
		this.pos = pos;
		carriedBox = null;
//		graph = new Graphics2D();

		lastTime = System.nanoTime();
		Timer scheduler = new Timer();
		scheduler.scheduleAtFixedRate(move, 500l, 500l); // update every half-second
	}

	public Position getPos() {
		return pos;
	}

	public AgvTask getCurrentTask() {
		return currentTask;
	}

	public void assignTask(AgvTask newTask) {
		if (currentTask != null) {
			// TODO: warn about unfinished task
		}
		currentTask = newTask;
	}

	public Position getPosition() {
		return pos;
	}

	public Graphics getDrawable() {
		return null;
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
	
//	private boolean targetReached(Position target)
//	{
//		return pos.subtract(target).length() < THRESHOLD;
//	}

	final TimerTask move = new TimerTask() {
		// this is periodically called to update the forklift's position
		public void run() {
		/*	long newTime = System.nanoTime();
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
//				vec = path.get(0).subtract(pos);
				// length of vector
//				Double len = vec.length();
				// distance we can move
				Double moveLen = timeElapsed / SPEED;
				// don't overshoot the target
//				moveLen = Math.min(moveLen, len);
				// normalize the vector and set it to the appropriate length
//				vec = vec.divide(len).multiply(moveLen);
				// add it to our position to obtain the new one
//				pos = pos.add(vec);

				// when we reach a target, remove it
				// due to floating point numbers we use a small range
//				if (targetReached(path.get(0))) {
//					path.remove(0);
//				}
//				if (currentTask != null) {
//					if (targetReached(currentTask.getPickup())) {
//						carriedBox = currentTask.getBox();
//						// TODO: Pick up Box
//					}
//					if (targetReached(currentTask.getDropoff())) {
//						carriedBox = null;
//						// TODO: Drop off Box
//					}
//				}
			}
			*/
		}
	};

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}
}
