package eu.codetopic.anty.ev3projectsbase.slam.base.mcl;

import java.util.Random;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Point;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.RectangleMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;

/**
 * Represents a particle for the particle filtering algorithm. The state of the
 * particle is the pose, which represents a possible pose of the robot.
 * <p>
 * The weight for a particle is set by taking a set of theoretical range readings using a
 * map of the environment, and comparing these ranges with those taken by the
 * robot. The weight represents the relative probability that the robot has this
 * pose. Weights are from 0 to 1.
 *
 * @author Lawrie Griffiths
 */
public class MCLParticle {

    private static final Random rand = new Random();
    private static boolean debug = false;

    // Instance variables (kept to minimum to allow maximum number of particles)
    private Pose pose;
    private float weight = 1;

    /**
     * Create a particle with a specific pose
     *
     * @param pose the pose
     */
    public MCLParticle(Pose pose) {
        this.pose = pose;
    }

    public static void setDebug(boolean yes) {
        debug = yes;
    }

    /**
     * Return the weight of this particle
     *
     * @return the weight
     */
    public float getWeight() {
        return weight;
    }

    /**
     * Set the weight for this particle
     *
     * @param weight the weight of this particle
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }

    /**
     * Return the pose of this particle
     *
     * @return the pose
     */
    public Pose getPose() {
        return pose;
    }

    /**
     * Calculate the weight for this particle by comparing its readings with the
     * robot's readings
     *
     * @param rr Robot readings
     */
    public void calculateWeight(ScanResults rr, RectangleMap map, float divisor) {
        weight = 1;
        Pose tempPose = new Pose();
        tempPose.setLocation(pose.getLocation());
        for (int i = 0; i < rr.size(); i++) {
            if (!map.isInside(tempPose.getLocation())) {
                weight = 0;
                return;
            }
            float angle = rr.getAngle(i);
            tempPose.setHeading(pose.getHeading() + angle);
            float robotReading = rr.getDistance(i);
            float range = map.getDistanceToWallFrom(tempPose);
            if (Float.isInfinite(range)) continue;
            float diff = robotReading - range;
            weight *= (float) Math.exp(-(diff * diff) / divisor);
        }
    }

    /**
     * Get a specific reading
     *
     * @param i the index of the reading
     * @return the reading
     */
    public float getReading(int i, ScanResults rr, RectangleMap map) {
        Pose tempPose = new Pose();
        tempPose.setLocation(pose.getLocation());
        tempPose.setHeading(pose.getHeading() + rr.getAngle(i));
        return map.getDistanceToWallFrom(tempPose);
    }

    public ScanResults getReadings(ScanResults rr, RectangleMap map) {
        ScanResults pr = new ScanResults(rr.size());

        for (int i = 0; i < rr.size(); i++) {
            pr.add(getReading(i, rr, map), rr.getMaxDistance(i), rr.getAngle(i));
        }
        return pr;
    }

    /**
     * Apply the robot's move to the particle with a bit of random noise.
     * Only works for rotate or travel movements.
     */
    public void applyMove(Pose newPose, float distanceNoiseFactor, float angleNoiseFactor) {
        //float ym = (move.getDistanceTraveled() * ((float) Math.sin(Math.toRadians(pose.getHeading()))));
        //float xm = (move.getDistanceTraveled() * ((float) Math.cos(Math.toRadians(pose.getHeading()))));
        //float am = move.getAngleTurned();
        float xm = newPose.getX() - pose.getX();
        float ym = newPose.getY() - pose.getY();
        float am = newPose.getHeading() - pose.getHeading();

        pose.setLocation(new Point((float) (pose.getX() + xm + (distanceNoiseFactor * xm * rand.nextGaussian())),
                (float) (pose.getY() + ym + (distanceNoiseFactor * ym * rand.nextGaussian()))));
        pose.setHeading((float) (pose.getHeading() + am + (angleNoiseFactor * rand.nextGaussian())));
        pose.setHeading((float) ((int) (pose.getHeading() + 0.5f) % 360));
    }
}
