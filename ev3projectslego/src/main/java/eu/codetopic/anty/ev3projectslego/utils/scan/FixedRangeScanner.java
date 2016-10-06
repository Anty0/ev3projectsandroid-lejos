package eu.codetopic.anty.ev3projectslego.utils.scan;

import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.navigation.RotateMoveController;
import lejos.utility.Delay;

public class FixedRangeScanner implements RangeScanner {

    private static final String LOG_TAG = "FixedRangeScanner";

    protected final RangeFinder rangeFinder;
    protected final RotateMoveController pilot;

    protected RangeReadings readings;
    protected float[] angles;

    public FixedRangeScanner(RotateMoveController pilot, RangeFinder rangeFinder) {
        this.pilot = pilot;
        this.rangeFinder = rangeFinder;
    }

    public RangeReadings getRangeValues() {
        if (readings == null || readings.getNumReadings() != angles.length) {
            readings = new RangeReadings(angles.length);
        }

        for (int i = 0; i < angles.length; i++) {
            float angle;
            if (i == 0) angle = angles[0];
            else angle = angles[i] - angles[i - 1];
            pilot.rotate(normalize(angle), false);
            Delay.msDelay(50);
            float range = rangeFinder.getRange();
            readings.setRange(i, angles[i], range);
        }
        pilot.rotate(normalize(-angles[angles.length - 1]), false);
        return readings;
    }

    public void setAngles(float[] angleSet) {
        angles = angleSet;
    }

    private float normalize(float angle) {
        if (angle < -180) angle += 360;
        if (angle > 180) angle -= 360;
        return angle;
    }

    public RotateMoveController getPilot() {
        return pilot;
    }

    public RangeFinder getRangeFinder() {
        return rangeFinder;
    }
}
