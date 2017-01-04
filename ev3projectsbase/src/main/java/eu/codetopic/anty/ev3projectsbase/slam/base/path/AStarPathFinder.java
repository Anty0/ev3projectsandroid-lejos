package eu.codetopic.anty.ev3projectsbase.slam.base.path;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eu.codetopic.anty.ev3projectsbase.slam.base.map.TileBasedMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.heuristic.ClosestHeuristic;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.target.KnownTargetDetector;

/**
 * A path finder implementation that uses the AStar heuristic based algorithm
 * to determine a path.
 *
 * @author Kevin Glass
 */
public class AStarPathFinder {
    /**
     * The map being searched
     */
    private final TileBasedMap map;
    /**
     * The maximum depth of search we're willing to accept before giving up
     */
    private final int maxDistance;
    /**
     * Size of vehicle in tiles
     */
    private final int vehicleSize;
    /**
     * True if we allow diagonal movement
     */
    private final boolean allowDiagonalMovement;
    /**
     * The heuristic we're applying to determine which nodes to search first
     */
    private final AStarHeuristic heuristic;

    /**
     * Create a path finder with the default heuristic - closest to target.
     *
     * @param map                   The map to be searched
     * @param maxDistance           The maximum depth we'll search before giving up
     * @param allowDiagonalMovement True if the search should try diagonal movement
     */
    public AStarPathFinder(TileBasedMap map, int maxDistance, int vehicleSize, boolean allowDiagonalMovement) {
        this(map, maxDistance, vehicleSize, allowDiagonalMovement, new ClosestHeuristic());
    }

    /**
     * Create a path finder
     *
     * @param heuristic             The heuristic used to determine the search order of the map
     * @param map                   The map to be searched
     * @param maxDistance           The maximum depth we'll search before giving up
     * @param allowDiagonalMovement True if the search should try diagonal movement
     */
    public AStarPathFinder(TileBasedMap map, int maxDistance, int vehicleSize,
                           boolean allowDiagonalMovement, AStarHeuristic heuristic) {
        this.map = map;
        this.maxDistance = maxDistance;
        this.vehicleSize = Math.max(1, vehicleSize);
        this.allowDiagonalMovement = allowDiagonalMovement;
        this.heuristic = heuristic;
    }

    @Nullable
    public Path findPath(Mover mover, int sx, int sy, int tx, int ty) {
        return findPath(mover, sx, sy, new KnownTargetDetector(tx, ty));
    }

    @Nullable
    public Path findPath(Mover mover, int sx, int sy, AStarTargetDetector targetDetector) {
        PathInfo pathInfo = new PathInfo();

        // easy first check, if the destination is blocked, we can't get there

        if (targetDetector.isTargetKnown(mover, map, sx, sy) && map.blocked(mover,
                targetDetector.getKnownTargetX(mover, map, sx, sy), targetDetector.getKnownTargetY(mover, map, sx, sy))) {
            return null;
        }

        // initial state for A*. The closed group is empty. Only the starting

        // tile is in the open list and it'e're already there
        pathInfo.open.add(pathInfo.getNode(sx, sy));

        Node targetNode = null;
        // while we haven'n't exceeded our max search depth
        int totalDistance = 0;
        while ((maxDistance == -1 || totalDistance < maxDistance) && !pathInfo.open.isEmpty()) {
            // pull out the first node in our open list, this is determined to

            // be the most likely to be the next step based on our heuristic

            Node current = pathInfo.open.first();
            if (targetDetector.isTarget(mover, map, sx, sy, current.getX(), current.getY())) {
                targetNode = current;
                break;
            }

            pathInfo.open.remove(current);
            pathInfo.closed.add(current);

            // search through all the neighbours of the current node evaluating

            // them as next steps

            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    // not a neighbour, its the current tile

                    if (x == 0 && y == 0) {
                        continue;
                    }

                    // if we're not allowing diagonal movement then only

                    // one of x or y can be set

                    if (!allowDiagonalMovement) {
                        if (x != 0 && y != 0) {
                            continue;
                        }
                    }

                    // determine the location of the neighbour and evaluate it

                    int xp = x + current.getX();
                    int yp = y + current.getY();

                    if (isValidLocation(mover, sx, sy, xp, yp)) {
                        // the cost to get to this node is cost the current plus the movement

                        // cost to reach this node. Note that the heursitic value is only used

                        // in the sorted open list

                        float nextStepCost = current.getCost() + getMovementCost(mover, current.getX(), current.getX(), xp, yp);
                        Node neighbour = pathInfo.getNode(xp, yp);
                        map.pathFinderVisited(xp, yp);

                        // if the new cost we've determined for this node is lower than

                        // it has been previously makes sure the node hasn'e've
                        // determined that there might have been a better path to get to

                        // this node so it needs to be re-evaluated

                        if (nextStepCost < neighbour.getCost()) {
                            pathInfo.open.remove(neighbour);
                            pathInfo.closed.remove(neighbour);
                        }

                        // if the node hasn't already been processed and discarded then

                        // reset it's cost to our current cost and add it as a next possible

                        // step (i.e. to the open list)

                        if (!pathInfo.open.contains(neighbour) && !pathInfo.closed.contains(neighbour)) {
                            neighbour.setCost(nextStepCost);
                            neighbour.setHeuristic(getHeuristicCost(mover, sx, sy, xp, yp, targetDetector));
                            totalDistance = Math.max(totalDistance, neighbour.setParent(current));
                            pathInfo.open.add(neighbour);
                        }
                    }
                }
            }
        }

        // since we'e've run out of search
        // there was no path. Just return null

        //noinspection ConstantConditions
        if (targetNode == null) {
            return null;
        }

        // At this point we've definitely found a path so we can uses the parent

        // references of the nodes to find out way from the target location back

        // to the start recording the nodes on the way.

        //System.out.println(pathInfo);

        Path path = new Path();
        Node startNode = pathInfo.getNode(sx, sy);
        while (targetNode != startNode) {
            path.addStepToStart(targetNode.getX(), targetNode.getY());
            targetNode = targetNode.getParent();
        }
        path.addStepToStart(sx, sy);

        // that's it, we have our path

        return path;
    }

    /**
     * Check if a given location is valid for the supplied mover
     *
     * @param mover The mover that would hold a given location
     * @param sx    The starting x coordinate
     * @param sy    The starting y coordinate
     * @param x     The x coordinate of the location to check
     * @param y     The y coordinate of the location to check
     * @return True if the location is valid for the given mover
     */
    public boolean isValidLocation(Mover mover, int sx, int sy, int x, int y) {
        if (!checkLocation(mover, sx, sy, x, y)) return false;
        for (int s = 1; s < vehicleSize; s++) {
            for (int i = -s; i <= s; i++) {
                if (!checkLocation(mover, sx, sy, x - i, y - s)
                        || !checkLocation(mover, sx, sy, x - i, y + s)
                        || !checkLocation(mover, sx, sy, x + s, y - i)
                        || !checkLocation(mover, sx, sy, x - s, y - i)) return false;
            }
        }
        return true;
    }

    private boolean checkLocation(Mover mover, int sx, int sy, int x, int y) {
        return map.getBoundingRect().contains(x, y)
                && ((sx == x && sy == y) || !map.blocked(mover, x, y));
    }

    /**
     * Get the cost to move through a given location
     *
     * @param mover The entity that is being moved
     * @param sx    The x coordinate of the tile whose cost is being determined
     * @param sy    The y coordinate of the tile whose cost is being determined
     * @param tx    The x coordinate of the target location
     * @param ty    The y coordinate of the target location
     * @return The cost of movement through the given tile
     */
    public float getMovementCost(Mover mover, int sx, int sy, int tx, int ty) {
        return map.getCost(mover, sx, sy, tx, ty);
    }

    /**
     * Get the heuristic cost for the given location. This determines in which
     * order the locations are processed.
     *
     * @param mover          The entity that is being moved
     * @param x              The x coordinate of the tile whose cost is being determined
     * @param y              The y coordinate of the tile whose cost is being determined
     * @param targetDetector The target location detector
     * @return The heuristic cost assigned to the tile
     */
    public float getHeuristicCost(Mover mover, int sx, int sy, int x, int y, AStarTargetDetector targetDetector) {
        return heuristic.getCost(map, mover, sx, sy, x, y, targetDetector);
    }

    private static class PathInfo {
        /**
         * The set of nodes that have been searched through
         */
        public final ArrayList<Node> closed = new ArrayList<>();
        /**
         * The set of nodes that we do not yet consider fully searched
         */
        public final SortedList<Node> open = new SortedList<>();
        /**
         * The complete set of nodes across the map
         */
        private final Map<Point, Node> nodes = new HashMap<>();

        private static String fillToLen(CharSequence toFill, int len) {
            StringBuilder builder = new StringBuilder();
            for (int i = toFill.length(); i < len; i++)
                builder.append(" ");
            builder.append(toFill);
            return builder.toString();
        }

        public Node getNode(int x, int y) {
            return getNode(new Point(x, y));
        }

        public Node getNode(Point position) {
            Node node = nodes.get(position);
            if (node == null) {
                node = new Node(position);
                nodes.put(position, node);
            }
            return node;
        }

        @Override
        public String toString() {
            int minX, maxX, minY, maxY, maxXLen, maxYLen, maxCLen, maxHLen;
            minX = minY = Integer.MAX_VALUE;
            maxX = maxY = maxXLen = maxYLen = maxCLen = maxHLen = Integer.MIN_VALUE;
            for (Node node : nodes.values()) {
                minX = Math.min(minX, node.getX());
                maxX = Math.max(maxX, node.getX());
                minY = Math.min(minY, node.getY());
                maxY = Math.max(maxY, node.getY());

                maxXLen = Math.max(maxXLen, String.valueOf(node.getX()).length());
                maxYLen = Math.max(maxYLen, String.valueOf(node.getY()).length());
                maxCLen = Math.max(maxCLen, String.valueOf(node.getCost()).length());
                maxHLen = Math.max(maxHLen, String.valueOf(node.getHeuristic()).length());
            }

            StringBuilder nodesMap = new StringBuilder("\n");
            nodesMap.append(minX).append("-").append(maxX).append(", ")
                    .append(minY).append("-").append(maxY).append("\n");
            for (int y = minY - 1; y <= maxY + 1; y++) {
                nodesMap.append("\n{");
                for (int x = minX - 1; x <= maxX + 1; x++) {
                    Node node = nodes.get(new Point(x, y));
                    nodesMap.append(fillToLen(String.valueOf(x), maxXLen)).append(",")
                            .append(fillToLen(String.valueOf(y), maxYLen)).append(",")
                            .append(fillToLen(node == null ? "-" : String.valueOf(node.getCost()), maxCLen)).append(",")
                            .append(fillToLen(node == null ? "-" : String.valueOf(node.getHeuristic()), maxHLen)).append("|");
                }
                nodesMap.append("}");
            }
            nodesMap.append("\n");

            return "PathInfo{" +
                    "closed=" + closed +
                    ", open=" + open +
                    ", nodes=" + nodes +
                    ", nodesMap=" + nodesMap +
                    '}';
        }
    }

    /**
     * A simple sorted list
     *
     * @author kevin
     */
    private static class SortedList<T extends Comparable<T>> {
        /**
         * The list of elements
         */
        private ArrayList<T> list = new ArrayList<>();

        /**
         * Retrieve the first element from the list
         *
         * @return The first element from the list
         */
        public T first() {
            return list.get(0);
        }

        /**
         * Empty the list
         */
        public void clear() {
            list.clear();
        }

        /**
         * Add an element to the list - causes sorting
         *
         * @param o The element to add
         */
        public void add(T o) {
            list.add(o);
            Collections.sort(list);
        }

        /**
         * Remove an element from the list
         *
         * @param o The element to remove
         */
        public void remove(Object o) {
            //noinspection SuspiciousMethodCalls
            list.remove(o);
        }

        /**
         * Get the number of elements in the list
         *
         * @return The number of element in the list
         */
        public int size() {
            return list.size();
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }

        /**
         * Check if an element is in the list
         *
         * @param o The element to search for
         * @return True if the element is in the list
         */
        public boolean contains(Object o) {
            //noinspection SuspiciousMethodCalls
            return list.contains(o);
        }
    }

    private static class Point {

        private final int x, y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            return getX() == point.getX() && getY() == point.getY();

        }

        @Override
        public int hashCode() {// TODO: 27.10.16 use copy of Objects.hashcode() from JavaUtils
            int result = getX();
            result = 31 * result + getY();
            return result;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    /**
     * A single node in the search graph
     */
    private static class Node implements Comparable<Node> {

        private final Point position;
        /**
         * The path cost for this node
         */
        private float cost = 0;
        /**
         * The parent of this node, how we reached it in the search
         */
        private Node parent = null;
        /**
         * The heuristic cost of this node
         */
        private float heuristic = 0;
        /**
         * The search depth of this node
         */
        private int depth = 0;

        private Node(Point position) {
            this.position = position;
        }

        public int getX() {
            return position.getX();
        }

        public int getY() {
            return position.getY();
        }

        public Point getPosition() {
            return position;
        }

        public float getCost() {
            return cost;
        }

        public Node setCost(float cost) {
            this.cost = cost;
            return this;
        }

        /**
         * Set the parent of this node
         *
         * @param parent The parent node which lead us to this node
         * @return The depth we have no reached in searching
         */
        public int setParent(Node parent) {
            depth = parent.depth + 1;
            this.parent = parent;

            return depth;
        }

        public Node getParent() {
            return parent;
        }

        public float getHeuristic() {
            return heuristic;
        }

        public Node setHeuristic(float heuristic) {
            this.heuristic = heuristic;
            return this;
        }

        public int getDepth() {
            return depth;
        }

        public Node setDepth(int depth) {
            this.depth = depth;
            return this;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "position=" + position +
                    ", cost=" + cost +
                    ", parent=" + parent +
                    ", heuristic=" + heuristic +
                    ", depth=" + depth +
                    '}';
        }

        /**
         * @see Comparable#compareTo(Object)
         */
        public int compareTo(@NotNull Node other) {
            float f = heuristic + cost;
            float of = other.heuristic + other.cost;

            if (f < of) {
                return -1;
            } else if (f > of) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}