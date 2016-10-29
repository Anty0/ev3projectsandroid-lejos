package eu.codetopic.anty.ev3projectsbase.slam.base.map;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Point;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Rectangle;

/**
 * The RectangleMap interface supports determining the range to a feature on the map
 * (such as a wall), from an object with a specific pose.
 * <p>
 * It also supports the a method to determine if a point is within the mapped
 * area.
 *
 * @author Lawrie Griffiths
 */
public interface RectangleMap {
    /**
     * The the range to the nearest wall (or other feature)
     *
     * @param pose the pose of the robot
     * @return the distanceToWallFrom
     */
    float getDistanceToWallFrom(Pose pose);

    /**
     * Test if a point is within the mapped area
     *
     * @param p the point
     * @return true iff the point is within the mapped area
     */
    boolean isInside(Point p);

    /**
     * Get the bounding rectangle for the mapped area
     *
     * @return the bounding rectangle
     */
    Rectangle getBoundingRect();
}
