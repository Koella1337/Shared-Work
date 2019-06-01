package factory.subsystems.warehouse;

import java.awt.Graphics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.w3c.dom.Element;

import factory.database.Database;
import factory.database.StorageSiteTable;
import factory.shared.Constants;
import factory.shared.Constants.PlaceableSize;
import factory.shared.Container;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.TaskQueue;
import factory.shared.Transaction;
import factory.shared.Utils;
import factory.shared.enums.EventKind;
import factory.shared.enums.Material;
import factory.shared.enums.MaterialStatus;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Placeable;

public class StorageSite implements Placeable {
	
	/** If task.getTimeLeft() >= this the Task will be delayed. */
	private static final long IGNORE_DEADLINE_THRESHOLD = 70000;
	
	/** If above this threshold the StorageSite will always choose to do a Task. */
	private static final int TASK_AMOUNT_THRESHOLD = 3;
	
	/** If the MaterialStatus-Comparator returns above this threshold, fetching will be preferred over sorting. */
	private static final int SHELF_BOX_COMPARISON_THRESHOLD = -2;
	
	private static final int SIM_RESUPPLY_MIN_TIME = 10000;
	private static final int SIM_RESUPPLY_MAX_TIME = 50000;
	
	private final WarehouseSystem warehouseSystem;
	private final StorageSiteTable dbTable;
	private final WarehouseForklift forklift;
	
	private final int id;
	private final Position pos;
	
	private final List<Placeable> placeables;
	private final List<Shelf> shelves;
	private final TaskQueue<WarehouseTask> tasks;
	
	private final ResourceBox inputbox;
	private final ResourceBox outputbox;
	
	/** amount of containers currently incoming from resupplying */
	private AtomicInteger incomingContainerAmount = new AtomicInteger(0);	
	
	public StorageSite(WarehouseSystem warehouseSystem, int id, Element xmlStorageSiteElem) {
		Objects.requireNonNull(xmlStorageSiteElem);
		this.warehouseSystem = Objects.requireNonNull(warehouseSystem);
		
		//xml init
		this.pos = Utils.xmlGetPositionFromElement(xmlStorageSiteElem);
		this.inputbox = new ResourceBox(warehouseSystem, Utils.xmlGetPositionFromFirstChild(xmlStorageSiteElem, "inputbox"));
		this.outputbox = new ResourceBox(warehouseSystem, Utils.xmlGetPositionFromFirstChild(xmlStorageSiteElem, "outputbox"));
		
		//general init
		this.id = id;
		this.tasks = new TaskQueue<>();
		this.forklift = new WarehouseForklift(this, new Position(pos.xPos, pos.yPos));
		
		//interior init
		this.placeables = new ArrayList<>();
		placeables.add(inputbox);
		placeables.add(outputbox);
		this.shelves = buildShelves();
		placeables.addAll(shelves);
		placeables.add(forklift);		//add forklift last so it is drawn over other Placeables
		
		//database init
		this.dbTable = new StorageSiteTable(id);
		Database.INSTANCE.addTable(dbTable);
	}
	
	/** Creates as many shelves as possible to fit into this StorageSite's interior. */
	private List<Shelf> buildShelves() {
		final List<Shelf> shelves = new ArrayList<>();
		final PlaceableSize shelfSize = PlaceableSize.SHELF;
		
		final int shelfWidth = shelfSize.x;
		final int shelfHeight = shelfSize.y;
		final int shelfOffset = Constants.SHELF_OFFSET;
		
		final Position ibox = inputbox.getPosition().clone();
		ibox.xPos -= shelfOffset/2; ibox.xSize += shelfOffset;
		ibox.yPos -= shelfOffset/2; ibox.ySize += shelfOffset;
		final Position obox = outputbox.getPosition().clone();
		obox.xPos -= shelfOffset/2; obox.xSize += shelfOffset;
		obox.yPos -= shelfOffset/2; obox.ySize += shelfOffset;
		
		Position nextShelfPos = Utils.assignSize(new Position(pos.xPos + shelfOffset, pos.yPos + shelfOffset), shelfSize);
		
		while ( (nextShelfPos.xPos + shelfWidth) < (pos.xPos + pos.xSize - shelfOffset) ) {
			//build shelf column
			while ( (nextShelfPos.yPos + shelfHeight) < (pos.yPos + pos.ySize - shelfOffset) ) {
				//make sure no shelf overlaps with in-/output box
				if ( !(Position.isOverlapping(nextShelfPos, ibox) || Position.isOverlapping(nextShelfPos, obox)) ) {
					shelves.add(new Shelf(Utils.assignSize(nextShelfPos.clone(), shelfSize)));
				}
				nextShelfPos.yPos += shelfHeight;
			}
			//prepare for building next column
			nextShelfPos.xPos += shelfWidth + shelfOffset;
			nextShelfPos.yPos = pos.yPos + shelfOffset;
		}
		
		return shelves;
	}
	
	/** Fills shelves from database. Call this after Database.INSTANCE.initialize() */
	protected void initializeShelves() {
		try {
			Container[] containers = dbTable.getAllContainers();
			int shelvesLength = shelves.size();
			
			for (int i = 0; i < containers.length; i++) {
				shelves.get(i % shelvesLength).receiveContainer(containers[i]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}
	
	public List<Placeable> getPlaceables() {
		return placeables;
	}
	
	/** Returns a shelf with at least one free ItemSlot. */
	protected Shelf getFreeShelf() {
		for (Shelf shelf : shelves) {
			if (shelf.hasFreeSlot())
				return shelf;
		}
		throw new IllegalArgumentException(this.toString() + " is completely full!");
	}
	
	/** Returns a shelf from which the specified material can be fetched. */
	protected Shelf getShelfForMaterial(Material mat) {
		for (Shelf shelf : shelves) {
			if (shelf.hasMaterial(mat))
				return shelf;
		}
		throw new IllegalArgumentException("Material \"" + mat + "\" not present in " + this.toString());
	}
	
	protected int getContainerAmount(Material material) {
		try {
			return dbTable.getContainerAmount(material);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Accepts or rejects a task depending on whether the StorageSite is capable of<br>
	 * doing it within the deadline while also taking care of boxes and shelves.
	 * @return 
	 * 		"-1" ... required material not stored<br>
	 * 		" 0" ... task accepted <br>
	 * 		">0" ... rejected (overworked task count)
	 */
	protected int canAcceptTask(WarehouseTask task) {
		int containerAmount = getContainerAmount(task.getMaterial());
		if (containerAmount > 0)
			return tasks.size();
		else
			return -1;
	}
	
	/** Receive a Task from the WarehouseSystem. */
	protected void receiveTask(WarehouseTask task) {
		try {
			tasks.offer(task);	
			dbTable.removeMaterial(task.getMaterial(), 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected ResourceBox getOutputbox() {
		return outputbox;
	}

	protected ResourceBox getInputbox() {
		return inputbox;
	}

	@Override
	public Position getPosition() {
		return this.pos;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Constants.UI_BORDER_COLOR);
		g.drawRect(0, 0, pos.xSize, pos.ySize);
		g.drawString(this.toString(), 10, 10);
	}
	
	@Override
	public String toString() {
		return "(StorageSite " + this.id + ")";
	}

	protected void start() {
		try {
			// continous loop as daemon thread --> only stops at program exit
			while (true) {
				if (warehouseSystem.getStatus() == SubsystemStatus.RUNNING) {
					// choose between doing a task, sorting shelves and fetching from inputbox
					WarehouseTask task = tasks.peek();
					
					if (task != null && (tasks.size() > TASK_AMOUNT_THRESHOLD || task.getTimeLeft() < IGNORE_DEADLINE_THRESHOLD)) {
						this.performTask(tasks.poll());
					}
					else {	//decide between shelf-sorting and inputbox management
						final Comparator<MaterialStatus> materialStatusComparator = MaterialStatus.comparator();
						shelves.sort((shelf1, shelf2) -> {
							return materialStatusComparator.compare(shelf1.getShelfSortedness(), shelf2.getShelfSortedness());
						});
						Shelf shelfToSort = shelves.get(0);
						MaterialStatus shelfStatus = shelfToSort.getShelfSortedness();
						
						MaterialStatus inputboxStatus = inputbox.getFullness();
						
						boolean canSort = !(shelfStatus == MaterialStatus.PERFECT || shelfStatus == MaterialStatus.EMPTY);
						boolean canFetch = !(inputboxStatus == MaterialStatus.EMPTY);
						
						if (canFetch && materialStatusComparator.compare(shelfStatus, inputboxStatus) > SHELF_BOX_COMPARISON_THRESHOLD) {
							shelves.sort((shelf1, shelf2) -> {	//sort by emptiness
								boolean shelf1Empty = shelf1.isEmpty(), shelf2Empty = shelf2.isEmpty();
								if (shelf1Empty && shelf2Empty)
									return 0;
								else
									return shelf1Empty ? -1 : (shelf2Empty ? 1 : 0);
							});
							forklift.fetchFromInputbox(inputbox.getStoredMaterials()[0]);
						}
						else if (canSort) {
							forklift.sortShelf(shelfToSort);
						}
						else {
							if (task != null)
								this.performTask(tasks.poll());	//no sorting/fetching to do -> do task anyways
							else {
								//nothing to do -> move to startPos and wait 1s before querying again
								forklift.moveTo(new Position(pos.xPos, pos.yPos));
								Thread.sleep(1000);		
							}
								
						}
					}
					checkForResupply();
				}
				else {
					Thread.sleep(3000);		//system not running -> wait 3s before querying again
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/** Makes forklift do a Task, then notifies Monitoring about it. */
	private void performTask(WarehouseTask task) throws InterruptedException {
		forklift.doTask(Objects.requireNonNull(task));
		if (task.getTimeLeft() < 0) {
			warehouseSystem.notify(new FactoryEvent(warehouseSystem, EventKind.TASK_NOT_COMPLETED_BEFORE_DEADLINE, task));
		}
		warehouseSystem.notify(new FactoryEvent(warehouseSystem, EventKind.WAREHOUSE_TASK_COMPLETED, task, outputbox));
	}
	
	private void checkForResupply() {
		for (Material mat : Material.values()) {
			if (mat.toString().startsWith("CAR"))
				continue;
			
			try {
				int containerAmount = dbTable.getContainerAmount(mat);
				int missingAmount = Constants.WAREHOUSE_RESUPPLY_THRESHOLD - containerAmount;
				int orderAmount = Math.max(Constants.WAREHOUSE_RESUPPLY_MIN_AMOUNT, missingAmount);
				int predictedContainersInInput = inputbox.getContainerAmount() + orderAmount + incomingContainerAmount.get();
				
				if (missingAmount > 0 && predictedContainersInInput < Constants.RESOURCE_BOX_MAX_CONTAINERS) {
					resupply(mat, orderAmount);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void resupply(Material mat, int amount) {
		Random rng = new Random();
		if (Constants.DEBUG)
			System.out.printf("--> Resupplying %s with %-15s (amount: %d, incoming: %d)%n", this, mat, amount, incomingContainerAmount.get());
		
		try {
			dbTable.addMaterial(mat, amount);
			incomingContainerAmount.getAndAdd(amount);
			
			int transactionCost = (rng.nextInt(10) + 1) * amount;
			String date = Constants.DATE_FORMAT.format(new Date());
			warehouseSystem.addTransaction(new Transaction("Company for " + mat, mat, amount, transactionCost, date, id));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		new Thread(() -> {
			try {
				int bound = SIM_RESUPPLY_MAX_TIME - SIM_RESUPPLY_MIN_TIME;
				Thread.sleep(rng.nextInt(bound) + SIM_RESUPPLY_MIN_TIME);	//wait for order to arrive
				
				//add to inputbox
				synchronized(inputbox) {
					for (int i = 0; i < amount; i++)
						inputbox.receiveContainer(new Container(mat));
					incomingContainerAmount.getAndAdd(-amount);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
}
