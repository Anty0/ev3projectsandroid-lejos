package eu.codetopic.anty.ev3projectsbase.slam.base.mcl;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Rectangle2D;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.RectangleInt32;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.RectangleMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;

/**
 * Maintains an estimate of the robot pose using sensor data.  It uses Monte Carlo
 * Localization  (See section 8.3 of "Probabilistic Robotics" by Thrun et al. <br>
 * Uses a {@link MCLParticleSet} to represent the probability distribution  of the
 * estimated pose.
 *
 * @author Lawrie Griffiths and Roger Glassey
 */
public class MCLPoseProvider {

    private final MCLParticleSet particles;
    private final RectangleMap map;
    private final Pose pose = new Pose();

    private float minX, maxX, minY, maxY;
    private double varX, varY, varH;

    /**
     * Allocates a new MCLPoseProvider.
     *
     * @param map          - the RectangleMap
     * @param numParticles number of particles
     */
    public MCLPoseProvider(RectangleMap map, int numParticles, Pose initialPose) {
        this.pose.setLocation(initialPose.getX(), initialPose.getY());
        this.pose.setHeading(initialPose.getHeading());
        this.particles = new MCLParticleSet(map, numParticles, initialPose, 1f, 1f);
        this.map = map;
    }

    /**
     * Allocates a new MCLPoseProvider.
     *
     * @param map          - the RectangleMap
     * @param numParticles number of particles
     * @param border       of the map
     */
    public MCLPoseProvider(RectangleMap map, int numParticles, int border) {
        this.particles = new MCLParticleSet(map, numParticles, border);
        this.map = map;
    }

    /**
     * Returns the particle set
     *
     * @return the particle set
     */
    public MCLParticleSet getParticles() {
        return particles;
    }

    public void applyMove(Pose newPose) {
        particles.applyMove(newPose);
    }

    /**
     * Calculates particle weights from readings, then resample the particle set
     *
     * @param readings
     * @return true if update was successful.
     */
    public boolean update(ScanResults readings) {
        readings = (ScanResults) readings.clone();
        readings.filterUsable();
        boolean goodPose = particles.calculateWeights(readings, map);
        if (!goodPose) return false;
        goodPose = particles.resample();
        return goodPose;
    }

    /**
     * Returns the difference between max X and min X
     *
     * @return the difference between min and max X
     */
    public float getXRange() {
        return getMaxX() - getMinX();
    }

    /**
     * Return difference between max Y and min Y
     *
     * @return difference between max and min Y
     */
    public float getYRange() {
        return getMaxY() - getMinY();
    }

    /**
     * Returns the best best estimate of the current pose;
     *
     * @return the estimated pose
     */
    public Pose getPose() {
        estimatePose();
        return new Pose(pose);
    }

    public Pose getEstimatedPose() {
        return new Pose(pose);
    }

    /**
     * Estimate pose from weighted average of the particles
     * Calculate statistics
     */
    public void estimatePose() {
        float totalWeights = 0;
        float estimatedX = 0;
        float estimatedY = 0;
        float estimatedAngle = 0;
        varX = 0;
        varY = 0;
        varH = 0;
        minX = Float.MAX_VALUE;
        minY = Float.MAX_VALUE;
        maxX = Float.MIN_VALUE;
        maxY = Float.MIN_VALUE;

        for (int i = 0, len = particles.numParticles(); i < len; i++) {
            Pose p = particles.getParticle(i).getPose();
            float x = p.getX();
            float y = p.getY();
            //float weight = particles.getParticle(i).getWeight();
            float weight = 1; // weight is historic at this point, as resample has been done
            estimatedX += (x * weight);
            varX += (x * x * weight);
            estimatedY += (y * weight);
            varY += (y * y * weight);
            float head = p.getHeading();
            estimatedAngle += (head * weight);
            varH += (head * head * weight);
            totalWeights += weight;

            if (x < minX) minX = x;

            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        estimatedX /= totalWeights;
        varX /= totalWeights;
        varX -= (estimatedX * estimatedX);
        estimatedY /= totalWeights;
        varY /= totalWeights;
        varY -= (estimatedY * estimatedY);
        estimatedAngle /= totalWeights;
        varH /= totalWeights;
        varH -= (estimatedAngle * estimatedAngle);

        // Normalize angle
        while (estimatedAngle > 180) estimatedAngle -= 360;
        while (estimatedAngle < -180) estimatedAngle += 360;

        pose.setLocation(estimatedX, estimatedY);
        pose.setHeading(estimatedAngle);
    }

    /**
     * Returns the minimum rectangle enclosing all the particles
     *
     * @return rectangle : the minimum rectangle enclosing all the particles
     */
    public Rectangle2D getErrorRect() {
        return new RectangleInt32((int) minX, (int) minY,
                (int) (maxX - minX), (int) (maxY - minY));
    }

    /**
     * Returns the maximum value of  X in the particle set
     *
     * @return max X
     */
    public float getMaxX() {
        return maxX;
    }

    /**
     * Returns the minimum value of   X in the particle set;
     *
     * @return minimum X
     */
    public float getMinX() {
        return minX;
    }

    /**
     * Returns the maximum value of Y in the particle set;
     *
     * @return max y
     */
    public float getMaxY() {
        return maxY;
    }

    /**
     * Returns the minimum value of Y in the particle set;
     *
     * @return minimum Y
     */
    public float getMinY() {
        return minY;
    }

    /**
     * Returns the standard deviation of the X values in the particle set;
     *
     * @return sigma X
     */
    public float getSigmaX() {
        return (float) Math.sqrt(varX);
    }

    /**
     * Returns the standard deviation of the Y values in the particle set;
     *
     * @return sigma Y
     */
    public float getSigmaY() {
        return (float) Math.sqrt(varY);
    }

    /**
     * Returns the standard deviation of the heading values in the particle set;
     *
     * @return sigma heading
     */
    public float getSigmaHeading() {
        return (float) Math.sqrt(varH);
    }
}
