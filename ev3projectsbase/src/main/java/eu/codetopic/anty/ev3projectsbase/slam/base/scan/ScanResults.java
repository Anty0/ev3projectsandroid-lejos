package eu.codetopic.anty.ev3projectsbase.slam.base.scan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ScanResults extends ArrayList<ScanResult> {

    private static final String LOG_TAG = "ScanResults";

    public ScanResults() {
    }

    public ScanResults(int i) {
        super(i);
    }

    public ScanResults(Collection<? extends ScanResult> collection) {
        super(collection);
    }

    /**
     * Get a specific distance result
     *
     * @param i the result index
     * @return the distance value
     */
    public float getDistance(int i) {
        return get(i).getDistance();
    }

    public float getMaxDistance(int i) {
        return get(i).getMaxDistance();
    }

    public float getTotalMaxDistance() {
        float max = Float.MIN_VALUE;
        for (ScanResult result : this) {
            max = Math.max(max, result.getMaxDistance());
        }
        return max;
    }

    /**
     * Get the angle of a specific result
     *
     * @param index the index of the result
     * @return the angle in degrees
     */
    public float getAngle(int index) {
        return get(index).getAngle();
    }

    /**
     * Add the scan result
     *
     * @param distance the distance result
     * @param angle    the angle of the result relative to the robot heading
     */
    public void add(float distance, float maxDistance, float angle) {
        add(new ScanResult(distance, maxDistance, angle));
    }

    public void offset(float x, float y, float rotatingY) {
        if (x == 0f && y == 0f && rotatingY == 0f) return;
        for (int i = 0, size = size(); i < size; i++) {
            ScanResult result = get(i);
            double radiansAngle = Math.toRadians(result.getAngle());
            float oldDistance = result.getDistance();
            float oldMaxDistance = result.getMaxDistance();
            if (Float.isInfinite(oldDistance)) oldDistance = oldMaxDistance;

            double cosAngle = Math.cos(radiansAngle);
            double sinAngle = Math.sin(radiansAngle);
            double oldX = cosAngle * oldDistance;
            double oldY = sinAngle * oldDistance;
            double oldMaxX = cosAngle * oldMaxDistance;
            double oldMaxY = sinAngle * oldMaxDistance;

            double newX = oldX + x;
            double newY = oldY + y;
            double newMaxX = oldMaxX + x;
            double newMaxY = oldMaxY + y;

            double newAngle = Math.toDegrees(Math.atan2(newY, newX));
            double newDistance = result.isUsable() ? Math.sqrt(Math.pow(newX, 2) + Math.pow(newY, 2)) : Float.POSITIVE_INFINITY;
            double newMaxDistance = Math.sqrt(Math.pow(newMaxX, 2) + Math.pow(newMaxY, 2));

            newDistance += rotatingY;
            newMaxDistance += rotatingY;

            set(i, new ScanResult((float) newDistance, (float) newMaxDistance, (float) newAngle));
        }
    }

    public void filterUsable() {
        Iterator<ScanResult> iterator = iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().isUsable()) {
                iterator.remove();
            }
        }
    }
}
