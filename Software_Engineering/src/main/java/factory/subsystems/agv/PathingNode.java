package factory.subsystems.agv;

import java.util.LinkedList;
import java.util.List;

import factory.shared.Position;

public class PathingNode {

    public Position p;
    public PathingNode[][] nodeMap;
    public boolean wall;
    public PathingNode parent;

    public PathingNode(Position p, boolean wall, PathingNode[][] nodeMap)
    {
        this.p = p;
        this.wall = wall;
        this.nodeMap = nodeMap;
    }

    public List<PathingNode> neighbours()
    {
        List<PathingNode> result = new LinkedList<>();
        int x = p.xPos / 20;
        int y = p.yPos / 20;
        if (x > 0)
        {
            PathingNode n = nodeMap[x - 1][y];
            if (!n.wall)
            {
                result.add(n);
            }
        }
        if (x < nodeMap.length-1)
        {
            PathingNode n = nodeMap[x + 1][y];
            if (!n.wall)
            {
                result.add(n);
            }
        }
        if (y > 0)
        {
            PathingNode n = nodeMap[x][y - 1];
            if (!n.wall)
            {
                result.add(n);
            }
        }
        if (y <  nodeMap[0].length-1)
        {
            PathingNode n = nodeMap[x][y + 1];
            if (!n.wall)
            {
                result.add(n);
            }
        }
        return result;
    }

    // unused because I went with bfs
    public int score(PathingNode other)
    {
        return Math.abs(other.p.xPos - this.p.xPos) + Math.abs(other.p.yPos - this.p.yPos);
    }
}
