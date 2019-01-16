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
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.enums.EventKind;
import factory.shared.interfaces.Placeable;
import factory.subsystems.assemblyline.Robot;

public class Forklift implements Placeable {
	private static final double SPEED = 10000000; // distance moved per nanosecond (inverted for easier calculation)
	private static final double THRESHOLD = 0.001; // maximum distance to a target to have reached it
	private static final double COLLISION_RADIUS = 3; // minimum distance to all other forklifts to avoid collisions
	private static final double SAFETY_RADIUS = 20; // minimum distance to all other forklifts to avoid having to reroute
	private long lastTime; // last time the forklift's position was updated
	private boolean shutdown;
	private boolean paused = false;
	private int pauseTimer = 0;
	public boolean part1 = false; // part 1 or 2 of the journey?
	private boolean evading = false;

	public List<Position> path;
	private Position vec = new Position(0, -1); // this is for drawing, so it mustn't be null even at the start

    private static Image forkliftImage;
    private static Image forkliftImageLoaded;
    
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
		scheduler.scheduleAtFixedRate(move, 50l, 50l); // update every half-second
		
		forkliftImage = new ImageIcon("resources/Forklift-v3.png").getImage();
		forkliftImageLoaded = new ImageIcon("resources/Forklift-v3-loaded.png").getImage();
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
			resume();
			part1 = true;
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
		return Position.length(Position.subtractPosition(pos, target.getMiddlePoint())) < THRESHOLD;
	}
	
	private void checkForCollision()	
	{
		for(Forklift f : coordinator.getForklifts())
		{
			if(Position.length(Position.subtractPosition(f.pos, this.pos)) < COLLISION_RADIUS)
			{
				if(f != this)
				{
					coordinator.notify(new FactoryEvent(coordinator, EventKind.AGV_FORKLIFT_COLLISION, this, f));
				}
			}
			if(Position.length(Position.subtractPosition(f.pos, this.pos)) < SAFETY_RADIUS)
			{
				if(f != this)
				{
//					if(!f.paused)
//					{
//						// handle it before it turns into a collision
//						f.pause();
//					}
//					coordinator.requestReroute(this);
					if(!evading || !f.evading)
					{
						evasiveManeuver(f);
						f.evasiveManeuver(this);
					}
				}
			}
		}
	}
	
	private void evasiveManeuver(Forklift f)
	{
		evading = true;
		Position vec = Position.subtractPosition(f.pos, pos);
		vec = Position.multiply(vec, -2);
//		vec = Position.divide(vec, (int)Position.length(vec));
		vec = Position.addPosition(pos,vec);
		Position goal = part1?currentTask.getPickup().getPosition():currentTask.getDropoff().getPosition();
		for(int i = 0; i < 5; i++)
		{
			if(!path.isEmpty() && !path.get(0).equals(goal))
			{
				path.remove(0);
			}
		}
		path.add(0, vec);
	}
	
	private void pause()
	{
		if(!this.paused)
		{
			Pathfinder.pausedForklifts[(this.pos.xPos / Pathfinder.GRANULARITY)][(this.pos.yPos / Pathfinder.GRANULARITY)] = true;
	        
	        
	        for (int i = 0; i < Pathfinder.pausedForklifts.length; i++)
	        {
	            for (int j = 0; j < Pathfinder.pausedForklifts[0].length; j++)
	            {
	                System.out.print(Pathfinder.pausedForklifts[j][i] ? "X" : " ");
	            }
	            System.out.println();
	        }
		}
		paused = true;
	}
	
	private void unpause()
	{
		pauseTimer = 0;
		paused = false;
//		for(int i = -2; i < 3; i++)
//		{
//			for(int j = -2; j < 3; j++)
//			{
////				try
////				{
//					Pathfinder.pausedForklifts[(this.pos.xPos / Pathfinder.GRANULARITY)+i][(this.pos.yPos / Pathfinder.GRANULARITY)+j] = false;
////				}
////				catch(ArrayOutOfBoundsException e)
////				{
////					
////				}
//			}
//		}
		Pathfinder.pausedForklifts[(this.pos.xPos / Pathfinder.GRANULARITY)][(this.pos.yPos / Pathfinder.GRANULARITY)] = false;
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
			if(paused)
			{
				pauseTimer += timeElapsed;
				if(pauseTimer > 2000000000) // 2 seconds
				{
					unpause();
				}
				return;
			}
			checkForCollision();
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
				if(len > 0)
				{
					// Normalise the vector and set it to the appropriate length
					vec = Position.divide(Position.multiply(vec, (int)Math.round(moveLen)), (int)Math.round(len));
					// add it to our position to obtain the new one
					pos = Position.addPosition(pos,vec);
				}

				// when we reach a target, remove it
				// due to floating point numbers we use a small range
				if (targetReached(path.get(0))) {
					path.remove(0);
					evading = false;
				}
				if (currentTask != null) {
					if (targetReached(currentTask.getPickup().getPosition()) && part1) {
						carriedBox = currentTask.getPickup().deliverContainer(currentTask.getMaterial());
						part1 = false;
					}
					if (targetReached(currentTask.getDropoff().getPosition())) {
						currentTask.getDropoff().receiveContainer(carriedBox);
						carriedBox = null;
						AgvTask saveTask = currentTask;
						currentTask = null;
						coordinator.finishedTask(saveTask);
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
			g.drawImage(carriedBox==null?forkliftImage:forkliftImageLoaded, -sizeX/2, -sizeY/2, sizeX, sizeY, null);
			g2.rotate(-angle);
	}
}
