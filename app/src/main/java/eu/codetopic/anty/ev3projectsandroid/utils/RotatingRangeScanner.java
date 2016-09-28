package eu.codetopic.anty.ev3projectsandroid.utils;

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

        for (int i = 0; i < angles.length; i++) {
            head.rotateTo((int) (angles[i] * gearRatio));
            Delay.msDelay(50);
            float range = rangeFinder.getRange();
            readings.setRange(i, angles[i], range);
        }
        head.rotateTo(0);
        return readings;
    }

    public void setAngles(float[] angles) {
        this.angles = angles.clone();
    }

    public RegulatedMotor getHead() {
        return head;
    }

    public RangeFinder getRangeFinder() {
        return rangeFinder;
    }
}
