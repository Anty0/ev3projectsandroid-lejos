package eu.codetopic.anty.ev3projectslego.utils.scan.base;

import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;

public interface RangeScanner {

    //int MOTOR_SCAN_SPEED_SLOWEST = 15;
    int MOTOR_SCAN_SPEED_SLOW = 150;
    int MOTOR_SCAN_SPEED_FAST = 360;

    float getMaxDistance();

    ScanResults aroundScan(int speed);

    ScanResults rangeScan(int speed, int angleFrom, int angleTo);

    Scanner getScanner();
}
