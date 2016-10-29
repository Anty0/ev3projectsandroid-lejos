package eu.codetopic.anty.ev3projectsbase.slam.base.scan;

import java.io.Serializable;

/**
 * Represent a single scan result
 */
public class ScanResult implements Serializable {

    private float distance, maxDistance, angle;

    /**
     * Create the scan result
     *
     * @param distance    the distance result
     * @param maxDistance
     * @param angle       the angle relative to the heading
     */
    public ScanResult(float distance, float maxDistance, float angle) {
        this.distance = distance;
        this.maxDistance = maxDistance;
        this.angle = normalizeAngle(angle);
    }

    private float normalizeAngle(float angle) {// TODO: 27.10.16 maybe create faster way using division
        while (angle > 360f) angle -= 360f;
        while (angle < 0f) angle += 360f;
        return angle;
    }

    /**
     * Get the distance result
     *
     * @return the scan result
     */
    public float getDistance() {
        return distance;
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    /**
     * Get the angle of the scan result
     *
     * @return the angle relative to the robot heading
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Test if distance result is usable
     *
     * @return true if distance result is usable
     */
    public boolean isUsable() {
        return !Float.isInfinite(distance);
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "distance=" + distance +
                ", maxDistance=" + maxDistance +
                ", angle=" + angle +
                '}';
    }
}
