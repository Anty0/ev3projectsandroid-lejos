package eu.codetopic.anty.ev3projectslego.utils.scan;

import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class RotatingRangeScanner implements RangeScanner {

    public static final int MOTOR_SCAN_SPEED_GRAPHICS_FAST = 150;
    public static final int MOTOR_SCAN_SPEED_GRAPHICS_SLOW = 15;
    public static final int MOTOR_SCAN_SPEED_FASTEST_ALLOWED = 360;
    private static final String LOG_TAG = "RotatingRangeScanner";

    protected final RangeFinder rangeFinder;
    protected final RegulatedMotor head;
    protected final double gearRatio;

    protected RangeReadings readings;
    protected float[] angles = {0, 90};// default

    public RotatingRangeScanner(RegulatedMotor head, RangeFinder rangeFinder) {
        this(head, rangeFinder, 1);
    }

    public RotatingRangeScanner(RegulatedMotor head, RangeFinder rangeFinder, double gearRatio) {
        this.head = head;
        this.rangeFinder = rangeFinder;
        this.gearRatio = gearRatio;

        this.head.resetTachoCount();
        this.head.setSpeed(MOTOR_SCAN_SPEED_FASTEST_ALLOWED);
    }

    public RangeReadings getRangeValues() {
        if (readings == null || readings.getNumReadings() != angles.length) {
            readings = new RangeReadings(angles.length);
        }

        head.setSpeed(MOTOR_SCAN_SPEED_FASTEST_ALLOWED);

        for (int i = 0; i < angles.length; i++) {
            rotateTo(angles[i]);
            Delay.msDelay(50);
            float range = rangeFinder.getRange();
            readings.setRange(i, angles[i], range);
        }
        head.rotateTo(0);
        return readings;
    }

    public float[] getAngles() {
        return angles;
    }

    public void setAngles(float[] angles) {
        this.angles = angles.clone();
    }

    public RegulatedMotor getHead() {
        return head;
    }

    public void forward() {
        head.forward();
    }

    public void backward() {
        head.backward();
    }

    public void stop() {
        head.stop();
    }

    public void rotateTo(float angle) {
        rotateTo(angle, false);
    }

    public void rotateTo(float angle, boolean immediateReturn) {
        head.rotateTo((int) (angle * gearRatio), immediateReturn);
    }

    public void setSpeed(float speed) {
        head.setSpeed((int) (speed * gearRatio));
    }

    public float getTachoCount() {
        return (float) (head.getTachoCount() * gearRatio);
    }

    public RangeFinder getRangeFinder() {
        return rangeFinder;
    }
}
