package eu.codetopic.anty.ev3projectslego.mode.base;

import org.jetbrains.annotations.Nullable;

import eu.codetopic.anty.ev3projectsbase.slam.base.scan.SeekResult;
import eu.codetopic.anty.ev3projectslego.hardware.Hardware;
import eu.codetopic.anty.ev3projectslego.hardware.model.Model;
import eu.codetopic.anty.ev3projectslego.mode.ModeController;
import eu.codetopic.anty.ev3projectslego.utils.Range;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import eu.codetopic.anty.ev3projectslego.utils.scan.RotatingRangeScanner;
import eu.codetopic.anty.ev3projectslego.utils.scan.base.RangeScanner;
import eu.codetopic.anty.ev3projectslego.utils.scan.base.Scanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.MovePilot;

public final class BeaconFollowMode extends ModeController {

    @Override
    public boolean isSupported() {
        if (!Hardware.isSet()) return false;
        Model model = Hardware.get().getModel();
        if (model.getPilot() == null) return false;
        Scanner scanner = model.getScanner();
        return scanner != null && scanner.hasSeek();
    }

    @Override
    protected void onStart(@Nullable Canvas canvas /*TODO: use to draw beacon position*/) {
        if (!Hardware.isSet()) return;
        Model model = Hardware.get().getModel();
        RangeScanner rangeScanner = model.getRangeScanner();
        RegulatedMotor head = rangeScanner instanceof RotatingRangeScanner
                ? ((RotatingRangeScanner) rangeScanner).getHead() : null;
        Scanner scanner = model.getScanner();
        MovePilot pilot = model.getPilot();
        //TouchDetector touch = hardware.getTouch();// TODO: 21.9.16 add detection and maybe mapping of obstacles
        if (pilot == null || scanner == null || !scanner.hasSeek()) return;

        Range allowedScanRange = null;
        if (head != null) {
            head.rotateTo(0);
            allowedScanRange = new Range(-90, 90);// TODO: 15.10.16 maybe made this range modifiable
        }

        while (!Thread.interrupted()) {
            SeekResult result = scanner.fetchSeek();

            if (result == null) {
                if (head != null) head.stop();
                pilot.stop();
                Thread.yield();
                continue;
            }

            float beaconAngle = result.getBeaconPosAngle();
            if (head != null) {
                float scannerAngle = head.getTachoCount();
                if (beaconAngle > 0 && scannerAngle > allowedScanRange.getHigher()
                        || beaconAngle < 0 && scannerAngle < allowedScanRange.getLower()) {
                    head.stop();
                } else {
                    head.setSpeed((int) beaconAngle);
                    head.forward();
                }
                beaconAngle += scannerAngle;
            }

            if (result.getDistance() < 15f) {
                if (head != null) head.stop();
                pilot.stop();
                Thread.yield();
                continue;
            }

            pilot.arcForward(beaconAngle);
        }

        pilot.stop();
        if (head != null) head.rotateTo(0);
    }
}
