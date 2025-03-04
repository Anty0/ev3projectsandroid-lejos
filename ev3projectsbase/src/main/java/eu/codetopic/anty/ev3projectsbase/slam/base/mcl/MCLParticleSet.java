package eu.codetopic.anty.ev3projectsbase.slam.base.mcl;

import java.util.Random;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Point;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Rectangle;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.RectangleMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;

/**
 * Represents a particle set for the particle filtering algorithm.
 *
 * @author Lawrie Griffiths
 */
public class MCLParticleSet {

    private static int maxIterations = 1000;
    private static boolean debug = false;

    private final RectangleMap map;
    private final Rectangle boundingRect;

    private final int numParticles;
    private MCLParticle[] particles;

    private float twoSigmaSquared = 400f; // was 250 200
    private float distanceNoiseFactor = 0.2f;//0.2f;
    private float angleNoiseFactor = 15f;//4f;
    private float maxWeight;
    private int border = 10;    // The minimum distance from the edge of the map

    /**
     * Create a set of particles randomly distributed within the given map.
     *
     * @param map the map of the enclosed environment
     */
    public MCLParticleSet(RectangleMap map, int numParticles, int border) {
        this.map = map;
        this.numParticles = numParticles;
        this.border = border;
        boundingRect = map.getBoundingRect();
        particles = new MCLParticle[numParticles];
        for (int i = 0; i < numParticles; i++) {
            particles[i] = generateParticle();
        }
    }

    /**
     * Generates a set of particles within the map that have a minimum weight as
     * as calculated from the particle pose, the range readings and the map.
     *
     * @param map
     * @param numParticles - number of particles
     * @param border       - within which no particles should be generated
     * @param readings     - to use in calculating weight
     * @param divisor
     * @param minWeight    - the minimum wight of a particle in the map
     */
    public MCLParticleSet(RectangleMap map, int numParticles, int border,
                          ScanResults readings, float divisor, float minWeight) {
        if (debug) System.out.println("New  Particles from readings");
        int k = 1;
        this.map = map;
        this.numParticles = numParticles;
        this.border = border;
        boundingRect = map.getBoundingRect();
        particles = new MCLParticle[numParticles];
        MCLParticle particle;
        int i = 0;
        while (i < numParticles) {
            k++;
            particle = generateParticle();
            particle.calculateWeight(readings, map, divisor);
            if (minWeight < particle.getWeight()) {
                particles[i] = particle;
                i++;
                if (debug) System.out.println("generated " + i);
            }
        }
        System.out.println("Total particles tried " + k);
    }

    /**
     * Generates a circular cloud of particles centered on initialPose with random
     * normal radius  and angle, and random normal heading.
     *
     * @param map          the map
     * @param numParticles the number of particles
     * @param initialPose  the center of the cloud
     * @param radiusNoise  standard deviation of the normal of the distance from center
     * @param headingNoise standard deviation of heading
     */
    public MCLParticleSet(RectangleMap map, int numParticles, Pose initialPose,
                          float radiusNoise, float headingNoise) {
        this.map = map;
        this.numParticles = numParticles;
        border = 0;
        boundingRect = map.getBoundingRect();
        particles = new MCLParticle[numParticles];
        Random random = new Random();
        for (int i = 0; i < numParticles; i++) {
            float rad = radiusNoise * (float) random.nextGaussian();
            float theta = (float) (2 * Math.PI * Math.random());
            float x = initialPose.getX() + rad * (float) Math.cos(theta);
            float y = initialPose.getY() + rad * (float) Math.sin(theta);
            float heading = initialPose.getHeading() + headingNoise * (float) random.nextGaussian();
            particles[i] = new MCLParticle((new Pose(x, y, heading)));
            if (debug) {
                System.out.println(" new particle set ");
            }
        }
    }

    /**
     * Set system out debugging on or off
     *
     * @param debug true to set debug, false to set it off
     */
    public static void setDebug(boolean debug) {
        MCLParticleSet.debug = debug;
        if (debug)
            System.out.println("ParticleSet Debug ON ");
    }

    /**
     * Generate a random particle within the mapped area.
     *
     * @return the particle
     */
    private MCLParticle generateParticle() {
        float x, y, angle;
        Rectangle innerRect = new Rectangle(boundingRect.x + border, boundingRect.y + border,
                boundingRect.width - border * 2, boundingRect.height - border * 2);
        // Generate x, y values in bounding rectangle
        for (; ; ) { // infinite loop that we break out of when we have
            // generated a particle within the mapped area
            x = innerRect.x + (((float) Math.random()) * innerRect.width);
            y = innerRect.y + (((float) Math.random()) * innerRect.height);

            if (map.isInside(new Point(x, y))) break;
        }

        // Pick a random angle
        angle = ((float) Math.random()) * 360;

        return new MCLParticle(new Pose(x, y, angle));
    }

    /**
     * Return the number of particles in the set
     *
     * @return the number of particles
     */
    public int numParticles() {
        return numParticles;
    }

    /**
     * Get a specific particle
     *
     * @param i the index of the particle
     * @return the particle
     */
    public MCLParticle getParticle(int i) {
        return particles[i];
    }

    public MCLParticle[] getParticles() {
        return particles;
    }

    /**
     * Resample the set picking those with higher weights.
     *
     * Note that the new set has multiple instances of the particles with higher
     * weights.
     *
     * @return true iff lost
     */
    /**
     * Resample the set picking those with higher weights.
     * <p>
     * Note that the new set has multiple instances of the particles with higher
     * weights.
     *
     * @return true iff lost
     */
    public boolean resample() {
        // Rename particles as oldParticles and create a new set
        MCLParticle[] oldParticles = particles;
        particles = new MCLParticle[numParticles];

        // Continually pick a random number and select the particles with
        // weights greater than or equal to it until we have a full
        // set of particles.
        int count = 0;
        int iterations = 0;

        while (count < numParticles) {
            iterations++;
            if (iterations >= maxIterations) {
                if (debug) System.out.println("Lost: count = " + count);
                if (count > 0) { // Duplicate the ones we have so far
                    for (int i = count; i < numParticles; i++) {
                        particles[i] = new MCLParticle(particles[i % count].getPose());
                        particles[i].setWeight(particles[i % count].getWeight());
                    }
                    return false;
                } else { // Completely lost - generate a new set of particles
                    for (int i = 0; i < numParticles; i++) {
                        particles[i] = generateParticle();
                    }
                    return true;
                }
            }
            float rand = (float) Math.random();
            for (int i = 0; i < numParticles && count < numParticles; i++) {
                if (oldParticles[i].getWeight() >= rand) {
                    Pose p = oldParticles[i].getPose();
                    float x = p.getX();
                    float y = p.getY();
                    float angle = p.getHeading();

                    // Create a new instance of the particle and set its weight
                    particles[count] = new MCLParticle(new Pose(x, y, angle));
                    particles[count++].setWeight(oldParticles[i].getWeight());
                }
            }
        }
        return true;
    }


    /**
     * Calculate the weight for each particle
     *
     * @param rr the robot range readings
     */
    public boolean calculateWeights(ScanResults rr, RectangleMap map) {
        if (debug)
            System.out.println(" Calc weights using ranges:  " + rr.getDistance(0) + " " + rr.getDistance(1) + " "
                    + rr.getDistance(2) + " A "
                    + rr.getAngle(0) + " " + rr.getAngle(1) + " " + rr.getAngle(2));
        /*if (rr.incomplete()) {
            if (debug) System.out.println("range set incomplete");
            return false;
        }*/
        int zeros = 0;
        maxWeight = 0f;
        for (int i = 0; i < numParticles; i++) {
            particles[i].calculateWeight(rr, map, twoSigmaSquared);
            float weight = particles[i].getWeight();
            if (weight > maxWeight) maxWeight = weight;
            if (weight == 0) zeros++;
        }

        if (debug) System.out.println("Calc Weights Max wt " + maxWeight + " Zeros " + zeros);
        return maxWeight >= .01;
    }

    /**
     * Apply a move to each particle
     *
     * @param newPose the new pose to apply
     */
    public void applyMove(Pose newPose) {
        if (newPose == null) {
            System.out.println("applyMove: null move");
            return;
        }
        if (debug) System.out.println("particles applyMove " + newPose);
        maxWeight = 0f;
        for (int i = 0; i < numParticles; i++) {
            particles[i].applyMove(newPose, distanceNoiseFactor, angleNoiseFactor);
        }
        if (debug) System.out.println("particles applyMove Exit");
    }

    /**
     * The highest weight of any particle
     *
     * @return the highest weight
     */
    public float getMaxWeight() {
        float wt = 0;
        for (int i = 0; i < particles.length; i++) wt = Math.max(wt, particles[i].getWeight());
        return wt;
    }

    /**
     * Get the border where particles should not be generated
     *
     * @return the border
     */
    public float getBorder() {
        return border;
    }

    /**
     * Set border where no particles should be generated
     *
     * @param border the border
     */
    public void setBorder(int border) {
        this.border = border;
    }

    /**
     * Set the standard deviation for the sensor probability model
     *
     * @param sigma the standard deviation
     */
    public void setSigma(float sigma) {
        twoSigmaSquared = 2 * sigma * sigma;
    }

    /**
     * Set the distance noise factor
     *
     * @param factor the distance noise factor
     */
    public void setDistanceNoiseFactor(float factor) {
        distanceNoiseFactor = factor;
    }

    /**
     * Set the distance angle factor
     *
     * @param factor the distance angle factor
     */
    public void setAngleNoiseFactor(float factor) {
        angleNoiseFactor = factor;
    }

    /**
     * Set the maximum iterations for the resample algorithm
     *
     * @param max the maximum iterations
     */
    public void setMaxIterations(int max) {
        maxIterations = max;
    }

    /**
     * Find the index of the particle closest to a given co-ordinates.
     * This is used for diagnostic purposes.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the index
     */
    public int findClosest(float x, float y) {
        float minDistance = Float.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < numParticles; i++) {
            Pose pose = particles[i].getPose();
            float distance = (float) Math.sqrt((double) (
                    (pose.getX() - x) * (pose.getX() - x)) +
                    ((pose.getY() - y) * (pose.getY() - y)));
            if (distance < minDistance) {
                minDistance = distance;
                index = i;
            }
        }
        return index;
    }
}
