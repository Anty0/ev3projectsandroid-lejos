package eu.codetopic.anty.ev3projectslego.utils.scan;

import java.io.Closeable;
import java.io.IOException;

import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;
import eu.codetopic.anty.ev3projectslego.utils.scan.base.RangeScanner;
import eu.codetopic.anty.ev3projectslego.utils.scan.base.Scanner;
import lejos.robotics.RegulatedMotor;

public class RotatingRangeScanner implements RangeScanner, Closeable {

    private static final String LOG_TAG = "RotatingRangeScanner";

    protected final Scanner scanner;
    protected final float offsetX;
    protected final float offsetY;
    protected final RegulatedMotor head;
    protected final int aroundFrom;
    protected final int aroundTo;
    private final float offsetRotatingY;

    public RotatingRangeScanner(Scanner scanner, float offsetX, float offsetY, float offsetRotatingY,
                                RegulatedMotor head, int aroundAngleFrom, int aroundAngleTo) {

        this.scanner = scanner;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetRotatingY = offsetRotatingY;
        this.head = head;
        this.aroundFrom = aroundAngleFrom;
        this.aroundTo = aroundAngleTo;

        this.head.resetTachoCount();
        this.head.setSpeed(MOTOR_SCAN_SPEED_FAST);
    }

    @Override
    public float getMaxDistance() {
        return scanner.getMaxDistance() + Math.max(Math.abs(offsetX), Math.abs(offsetY));
    }

    @Override
    public synchronized ScanResults aroundScan(int speed) {
        head.stop();
        int tacho = head.getTachoCount();
        return Math.abs(tacho - aroundFrom) < Math.abs(tacho - aroundTo)
                ? rangeScan(speed, aroundFrom, aroundTo) : rangeScan(speed, aroundTo, aroundFrom);
    }

    @Override
    public synchronized ScanResults rangeScan(int speed, int angleFrom, int angleTo) {
        ScanResults results = new ScanResults(1024);// TODO: 15.10.16 calculate approximate amount of results based on speed (and add it as parameter)
        float maxDistance = getMaxDistance();

        head.setSpeed(MOTOR_SCAN_SPEED_FAST);
        head.rotateTo(angleFrom);
        head.setSpeed(speed);
        head.rotateTo(angleTo, true);

        while (head.isMoving() && !Thread.currentThread().isInterrupted()) {
            results.add(scanner.fetchDistance(), maxDistance, head.getTachoCount());
        }

        results.offset(offsetX, offsetY, offsetRotatingY);
        return results;
    }

    public synchronized RegulatedMotor getHead() {
        return head;
    }

    @Override
    public synchronized Scanner getScanner() {
        return scanner;
    }

    @Override
    public void close() throws IOException {
        head.stop();
        head.setSpeed(MOTOR_SCAN_SPEED_FAST);
        head.rotateTo(0);
        head.close();
    }
}
