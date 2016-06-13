/*    
    Copyright (C) 2012 http://software-talk.org/ (developer@software-talk.org)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * // TODO
 * possible optimizations:
 * - calculate f as soon as g or h are set, so it will not have to be
 *      calculated each time it is retrieved
 * - store nodes in openList sorted by their f value.
 */

package es.upm.common03.pathFinding;

import es.upm.common03.LocationUtility;
import es.upm.ontology.Location;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a simple map.
 * <p>
 * It's width as well as hight can be set up on construction.
 * The map can represent nodes that are walkable or not, it can be printed 
 * to sto, and it can calculate the shortest path between two nodes avoiding
 * walkable nodes.
 * <p>
 * <p>
 * Usage of this package:
 * Create a node class which extends AbstractNode and implements the sethCosts
 * method.
 * Create a NodeFactory that implements the NodeFactory interface.
 * Create Map instance with those created classes.
 * @see ExampleUsage ExampleUsage
 * <p>
 *
 * @see AbstractNode
 * @see NodeFactory
 * @version 1.0
 * @param <T>
 */
public class Map<T extends AbstractNode> {


    /** holds nodes. first dim represents x-, second y-axis. */
    private T[][] nodes;

    /** width + 1 is size of first dimension of nodes. */
    protected int width;
    /** height + 1 is size of second dimension of nodes. */
    protected int height;

    /** a Factory to create instances of specified nodes. */
    private NodeFactory nodeFactory;

    /**
     * constructs a squared map with given width and hight.
     * <p>
     * The nodes will be instanciated througth the given nodeFactory.
     *
     * @param width
     * @param height
     * @param nodeFactory 
     */
    public Map(int width, int height, NodeFactory nodeFactory) {
        // TODO check parameters. width and height should be > 0.
        this.nodeFactory = nodeFactory;        
        nodes = (T[][]) new AbstractNode[width+1][height+1];
        this.width = width;
        this.height = height;
        initEmptyNodes();
    }

    /**
     * initializes all nodes. Their coordinates will be set correctly.
     */
    private void initEmptyNodes() {
        for (int i = 1; i <= width; i++) {
            for (int j = 1; j <= height; j++) {
                nodes[i][j] = (T) nodeFactory.createNode(i, j);
            }
        }
    }

    public void resetAllWalkable(){
        for (int i = 1; i <= width; i++) {
            for (int j = 1; j <= height; j++) {
                setWalkable(i, j, true);
            }
        }
    }

    /**
     * sets nodes walkable field at given coordinates to given value.
     * <p>
     * x/y must be bigger or equal to 0 and smaller or equal to width/hight.
     *
     * @param x
     * @param y
     * @param bool
     */
    public void setWalkable(int x, int y, boolean bool) {
        // TODO check parameter.
        nodes[x][y].setWalkable(bool);
    }

    /**
     * returns node at given coordinates.
     * <p>
     * x/y must be bigger or equal to 0 and smaller or equal to width/hight.
     *
     * @param x
     * @param y
     * @return node
     */
    public final T getNode(int x, int y) {
        // TODO check parameter.
        return nodes[x][y];
    }

    /**
     * prints map to sto. Feel free to override this method.
     * <p>
     * a player will be represented as "o", an unwakable terrain as "#".
     * Movement penalty will not be displayed.
     */
    public void drawMap() {
        for (int i = 1; i <= width; i++) {
                print(" _"); // boarder of map
        }
        print("\n");

        for (int j = height; j >= 1; j--) {
            print("|"); // boarder of map
            for (int i = 0; i <= width; i++) {
                if (nodes[i][j] != null && nodes[i][j].isWalkable()) {
                    print("  ");
                } else {
                    print(" #"); // draw unwakable
                }
            }
            print("|\n"); // boarder of map
        }

        for (int i = 0; i <= width; i++) {
                print(" _"); // boarder of map
        }
    }

    /**
     * prints something to sto.
     */
    private void print(String s) {
        System.out.print(s);
    }

    /** list containing nodes not visited but adjacent to visited nodes. */
    private List<T> openList;
    /** list containing nodes already visited/taken care of. */
    private List<T> closedList;
    /** done finding path? */
    private boolean done = false;

    /**
     * finds an allowed path from start to goal coordinates on this map.
     * <p>
     * This method uses the A* algorithm. The hCosts value is calculated in
     * the given Node implementation.
     * <p>
     * This method will return a LinkedList containing the start node at the
     * beginning followed by the calculated shortest allowed path ending
     * with the end node.
     * <p>
     * If no allowed path exists, an empty list will be returned.
     * <p>
     * <p>
     * x/y must be bigger or equal to 0 and smaller or equal to width/hight.
     *
     * @param oldX
     * @param oldY
     * @param newX
     * @param newY
     * @return
     */
    public final List<T> findPath(int oldX, int oldY, int newX, int newY) {
        // TODO check input
        openList = new LinkedList<T>();
        closedList = new LinkedList<T>();
        openList.add(nodes[oldX][oldY]); // add starting node to open list

        done = false;
        T current;
        while (!done) {
            current = lowestFInOpen(); // get node with lowest fCosts from openList
            closedList.add(current); // add current node to closed list
            openList.remove(current); // delete current node from open list

            if ((current.getxPosition() == newX)
                    && (current.getyPosition() == newY)) { // found goal
                return calcPath(nodes[oldX][oldY], current);
            }

            // for all adjacent nodes:
            List<T> adjacentNodes = getAdjacent(current);
            for (int i = 0; i < adjacentNodes.size(); i++) {
                T currentAdj = adjacentNodes.get(i);
                if (!openList.contains(currentAdj) && !closedList.contains(currentAdj)) { // node is not in openList
                    currentAdj.setPrevious(current); // set current node as previous for this node
                    currentAdj.sethCosts(nodes[newX][newY]); // set h costs of this node (estimated costs to goal)
                    currentAdj.setgCosts(current); // set g costs of this node (costs from start to this node)
                    openList.add(currentAdj); // add node to openList
                } else { // node is in openList
                    if (currentAdj.getgCosts() > currentAdj.calculategCosts(current)) { // costs from current node are cheaper than previous costs
                        currentAdj.setPrevious(current); // set current node as previous for this node
                        currentAdj.setgCosts(current); // set g costs of this node (costs from start to this node)
                    }
                }
            }

            if (openList.isEmpty()) { // no path exists
                return new LinkedList<T>(); // return empty list
            }
        }
        return null; // unreachable
    }

    /**
     * calculates the found path between two points according to
     * their given <code>previousNode</code> field.
     *
     * @param start
     * @param goal
     * @return
     */
    private List<T> calcPath(T start, T goal) {
     // TODO if invalid nodes are given (eg cannot find from
     // goal to start, this method will result in an infinite loop!)
        LinkedList<T> path = new LinkedList<T>();
        if(start.equals(goal)){
            path.add(start);
            return path;
        }

        T curr = goal;
        boolean done = false;
        while (!done) {
            path.addFirst(curr);
            curr = (T) curr.getPrevious();

            if (curr.equals(start)) {
                done = true;
            }
        }
        return path;
    }

    /**
     * returns the node with the lowest fCosts.
     *
     * @return
     */
    private T lowestFInOpen() {
        T cheapest = openList.get(0);
        for (int i = 0; i < openList.size(); i++) {
            if (openList.get(i).getfCosts() < cheapest.getfCosts()) {
                cheapest = openList.get(i);
            }
        }
        return cheapest;
    }

    /**
     * returns a LinkedList with nodes adjacent to the given node.
     * if those exist, are walkable and are not already in the closedList!
     */
    private List<T> getAdjacent(T node) {
        // TODO make loop
        int x = node.getxPosition();
        int y = node.getyPosition();
        List<T> adj = new LinkedList<T>();

        T temp;
        for(int dir = 1; dir <= 6; dir++){
            Location loc = new Location();
            loc.setX(x);
            loc.setY(y);
            loc = LocationUtility.calculateNewLocation(loc, dir, width, height);
            temp = this.getNode(loc.getX(), loc.getY());
            adj.add(temp);
        }

        return adj;
    }

}
