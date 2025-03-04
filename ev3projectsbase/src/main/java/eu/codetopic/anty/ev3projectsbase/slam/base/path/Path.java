package eu.codetopic.anty.ev3projectsbase.slam.base.path;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A path determined by some path finding algorithm. A series of steps from
 * the starting location to the target location. This includes a step for the
 * initial location.
 *
 * @author Kevin Glass
 */
public class Path extends ArrayList<Path.Step> {

    public Path() {
    }

    public Path(int i) {
        super(i);
    }

    public Path(Collection<? extends Step> collection) {
        super(collection);
    }

    /**
     * Get the step at a given index in the path
     *
     * @param index The index of the step to retrieve. Note this should
     *              be >= 0 and < getLength();
     * @return The step information, the position on the map.
     */
    public Step getStep(int index) {
        return get(index);
    }

    /**
     * Get the x coordinate for the step at the given index
     *
     * @param index The index of the step whose x coordinate should be retrieved
     * @return The x coordinate at the step
     */
    public int getX(int index) {
        return getStep(index).x;
    }

    /**
     * Get the y coordinate for the step at the given index
     *
     * @param index The index of the step whose y coordinate should be retrieved
     * @return The y coordinate at the step
     */
    public int getY(int index) {
        return getStep(index).y;
    }

    /**
     * Append a step to the path.
     *
     * @param x The x coordinate of the new step
     * @param y The y coordinate of the new step
     */
    public void addStep(int x, int y) {
        add(new Step(x, y));
    }

    /**
     * Prepend a step to the path.
     *
     * @param x The x coordinate of the new step
     * @param y The y coordinate of the new step
     */
    public void addStepToStart(int x, int y) {
        add(0, new Step(x, y));
    }

    /**
     * Check if this path contains the given step
     *
     * @param x The x coordinate of the step to check for
     * @param y The y coordinate of the step to check for
     * @return True if the path contains the given step
     */
    public boolean contains(int x, int y) {
        return contains(new Step(x, y));
    }

    public Step nextStep() {
        if (isEmpty()) return null;
        return remove(0);
    }

    /**
     * A single step within the path
     *
     * @author Kevin Glass
     */
    public class Step {
        /**
         * The x coordinate at the given step
         */
        private int x;
        /**
         * The y coordinate at the given step
         */
        private int y;

        /**
         * Create a new step
         *
         * @param x The x coordinate of the new step
         * @param y The y coordinate of the new step
         */
        public Step(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Get the x coordinate of the new step
         *
         * @return The x coodindate of the new step
         */
        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        /**
         * Get the y coordinate of the new step
         *
         * @return The y coodindate of the new step
         */
        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        /**
         * @see Object#hashCode()
         */
        public int hashCode() {
            return x * y;
        }

        /**
         * @see Object#equals(Object)
         */
        public boolean equals(Object other) {
            if (other instanceof Step) {
                Step o = (Step) other;

                return (o.x == x) && (o.y == y);
            }

            return false;
        }
    }
}