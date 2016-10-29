package eu.codetopic.anty.ev3projectslego.utils.scan;

import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;
import eu.codetopic.anty.ev3projectslego.utils.scan.base.RangeScanner;
import eu.codetopic.anty.ev3projectslego.utils.scan.base.Scanner;
import lejos.robotics.navigation.RotateMoveController;

public class FixedRangeScanner implements RangeScanner {// warning speed is not supported

    private static final String LOG_TAG = "FixedRangeScanner";

    protected final Scanner scanner;
    protected final RotateMoveController pilot;
    private final float offsetY;

    public FixedRangeScanner(Scanner scanner, float offsetY, RotateMoveController pilot) {
        this.scanner = scanner;
        this.offsetY = offsetY;
        this.pilot = pilot;
    }

    @Override
    public float getMaxDistance() {
        return scanner.getMaxDistance();
    }

    @Override
    public ScanResults aroundScan(int speed) {// warning speed is not supported
        return rangeScan(speed, 0, 360);
    }

    @Override
    public ScanResults rangeScan(int speed, int angleFrom, int angleTo) {// warning speed is not supported
        ScanResults results = new ScanResults(1024);// TODO: 15.10.16 calculate approximate amount of results (and add it as parameter)
        float maxDistance = getMaxDistance();

        pilot.rotate(angleFrom, false);

        for (int i = angleFrom; i < angleTo; i += 20) {
            if (Thread.currentThread().isInterrupted()) break;
            pilot.rotate(20);
            results.add(scanner.fetchDistance(), maxDistance, i);
        }

        results.offset(0f, 0f, offsetY);
        return results;
    }

    public RotateMoveController getPilot() {
        return pilot;
    }

    @Override
    public Scanner getScanner() {
        return scanner;
    }
}
