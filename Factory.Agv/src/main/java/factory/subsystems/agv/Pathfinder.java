package factory.subsystems.agv;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import factory.shared.Position;
import factory.shared.Utils;
import factory.shared.interfaces.Placeable;

public class Pathfinder {


    /**
     * @param args the command line arguments
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public boolean[][] collisionMap;
    public static boolean[][] pausedForklifts;
    private PathingNode[][] nodeMap;
//    private AgvCoordinator coordinator;
    public static final int GRANULARITY = 5;
    
//    private void printCollisionMap()-*
//    {
//    	for(int i = 0; i < collisionMap.length; i++)
//    	{
//    		for(int j = 0; j < collisionMap[0].length; j++)
//    		{
//    			System.out.print(collisionMap[j][i]?"W":" ");
//    		}
//    		System.out.println();
//    	}
//		System.out.println();
//    }

    public Pathfinder(AgvCoordinator coordinator, Element factory, List<Placeable> freeParking) throws ParserConfigurationException, SAXException, IOException
    {
//    	this.coordinator = coordinator;
        Position factorySize = Utils.parsePosition(((Element) factory.getElementsByTagName("size").item(0)).getFirstChild().getNodeValue(), null);
        collisionMap = new boolean[factorySize.xPos / GRANULARITY][factorySize.yPos / GRANULARITY];        
        pausedForklifts = new boolean[factorySize.xPos / GRANULARITY][factorySize.yPos / GRANULARITY];

        NodeList storagesites = ((Element) factory.getElementsByTagName("warehouse").item(0)).getElementsByTagName("storagesite");
        NodeList assemblyLines = ((Element) factory.getElementsByTagName("assemblylines").item(0)).getElementsByTagName("assemblyline");
        Element staffquarters = (Element) factory.getElementsByTagName("staffquarter").item(0);

        // Generate collisionMap
        for (int i = 0; i < storagesites.getLength(); i++)
        {
            addObstacle(Utils.xmlGetPositionFromElement((Element) storagesites.item(i)));
        }
        addObstacle(Utils.xmlGetPositionFromElement(staffquarters));
        for (int i = 0; i < assemblyLines.getLength(); i++)
        {
            // String direction = ((Element)((Element) assemblyLines.item(i)).getElementsByTagName("direction").item(0)).getNodeValue();
            addAssemblyLine(Utils.xmlGetPositionFromElement((Element) assemblyLines.item(i)));
        }
        // remove free parking spaces!
        for (Placeable p : freeParking)
        {
            removeFreeParking(p);
        }
        
        
//        for (int i = 0; i < collisionMap.length; i++)
//        {
//            for (int j = 0; j < collisionMap[0].length; j++)
//            {
//                System.out.print(collisionMap[j][i] ? "X" : " ");
//            }
//            System.out.println();
//        }
        

        // Generate nodemap
        nodeMap = new PathingNode[collisionMap.length][collisionMap[0].length];
        for (int i = 0; i < collisionMap.length; i++)
        {
            for (int j = 0; j < collisionMap[0].length; j++)
            {
                nodeMap[i][j] = new PathingNode(new Position(i * GRANULARITY, j * GRANULARITY), collisionMap[i][j], nodeMap);
            }
        }
//
//        PathingNode path = findPath(new Position(400, 400), new Position(500, 900));
//
//        boolean[][] pathMap = new boolean[50][50];
//        while (path.parent != null)
//        {
//            System.out.println(path.p);	
//            pathMap[path.p.xPos/GRANULARITY][path.p.yPos/GRANULARITY] = true;
//            path = path.parent;
//        }
//        System.out.println(path.p);
//        pathMap[path.p.xPos/GRANULARITY][path.p.yPos/GRANULARITY] = true;
//
//        for (int i = 0; i < collisionMap.length; i++)
//        {
//            for (int j = 0; j < collisionMap[0].length; j++)
//            {
//                System.out.print(collisionMap[j][i] ? "X" : pathMap[j][i] ? "O" : " ");
//            }
//            System.out.println();
//        }
    }

    private void removeFreeParking(Placeable pl) 
    {
    	Position p = pl.getPosition();
        for (int x = 0; x < p.xSize; x = x + GRANULARITY)
        {
            for (int y = 0; y < p.ySize; y = y + GRANULARITY)
            {
                collisionMap[(x + p.xPos) / GRANULARITY][(y + p.yPos) / GRANULARITY] = false;
            }
        }
	}

	private void addObstacle(Position p)
    {
		int x = 0;
		int y = 0;
        for (x = 0; x < p.xSize; x = x + GRANULARITY)
        {
            for (y = 0; y < p.ySize; y = y + GRANULARITY)
            {
                collisionMap[(x + p.xPos) / GRANULARITY][(y + p.yPos) / GRANULARITY] = true;
            }
            try
            {
            	collisionMap[(x + p.xPos) / GRANULARITY][(y + p.yPos) / GRANULARITY] = true;
            }
            catch(ArrayIndexOutOfBoundsException e) // an diesem punkt isses mir wurscht
            {
            	
            }
        }
        try
        {
        	collisionMap[(x + p.xPos) / GRANULARITY][(y + p.yPos) / GRANULARITY] = true;
        }
        catch(ArrayIndexOutOfBoundsException e) // an diesem punkt isses mir wurscht
        {
        	
        }
    }

    private void addAssemblyLine(Position p)
    {
        // turns out, the direction is irrelevant for the agvsystem, who would have thought?
        p.xSize = 350;
        p.ySize = 100;
        addObstacle(p);
    }
    
    public synchronized List<Position> getPath(Position start, Position goal)
    {
    	PathingNode end = findPathNodes(start, goal);
    	if(end == null)
    	{
    		return null;
    	}
    	List<Position> result = new LinkedList<>();
    	
	    while (end.parent != null)
	    {
	        result.add(0, end.p);
	        end = end.parent;
	    }
        result.add(0, end.p);
    	
    	return result;
    }

    public PathingNode findPathNodes(Position start, Position goal)
    {
        // can't reach something inside a wall
        PathingNode startNode = getNodeFromPos(start);
        PathingNode endNode = getNodeFromPos(goal);
        if (startNode.wall || endNode.wall)
        {
            return null;
        }

        PathingNode currentNode = startNode;
        currentNode.parent = null;

        Queue<PathingNode> nodeQueue = new ArrayDeque<>();
        nodeQueue.add(currentNode);

        HashMap<Integer, PathingNode> visitedNodes = new HashMap<>();

        while (true)
        {
            if (nodeQueue.isEmpty())
            {
                break;
            }

            currentNode = nodeQueue.poll();

            if (currentNode == endNode)
            {
                return currentNode;
            }

//            if (currentNode.isLeaf())
//            {
//                continue;
//            }
            if (!visitedNodes.containsKey(currentNode.hashCode()))
            {
                visitedNodes.put(currentNode.hashCode(), currentNode);
                List<PathingNode> adjacent = currentNode.neighbours();
                for (PathingNode pn : adjacent)
                {
                    if (!visitedNodes.containsKey(pn.hashCode()))
                    {
                        pn.parent = currentNode;
                    }
                }
                nodeQueue.addAll(adjacent);
            }

        }

        return null;
    }

    private PathingNode getNodeFromPos(Position p)
    {
        return nodeMap[p.xPos / GRANULARITY][p.yPos / GRANULARITY];
    }
}
