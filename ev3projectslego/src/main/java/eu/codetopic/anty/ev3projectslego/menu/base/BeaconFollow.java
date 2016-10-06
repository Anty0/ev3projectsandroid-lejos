package eu.codetopic.anty.ev3projectslego.menu.base;

import eu.codetopic.anty.ev3projectslego.hardware.Hardware;
import eu.codetopic.anty.ev3projectslego.hardware.model.Model;
import eu.codetopic.anty.ev3projectslego.menu.BaseMode;
import eu.codetopic.anty.ev3projectslego.utils.Range;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import eu.codetopic.anty.ev3projectslego.utils.menu.Menu;
import eu.codetopic.anty.ev3projectslego.utils.menu.MenuItem;
import eu.codetopic.anty.ev3projectslego.utils.scan.DistanceRangeFinder;
import eu.codetopic.anty.ev3projectslego.utils.scan.DistanceRangeFinder.SeekResult;
import eu.codetopic.anty.ev3projectslego.utils.scan.RotatingRangeScanner;
import lejos.robotics.RangeScanner;
import lejos.robotics.navigation.MovePilot;

public final class BeaconFollow extends BaseMode {

    public static boolean isSupported() {
        if (!Hardware.isSet()) return false;
        Model model = Hardware.get().getModel();
        return model.getPilot() != null && model.getDistanceRangeFinder() != null;
    }

    @Override
    public void run() {
        //Canvas canvas = getCanvas(); //TODO: use to draw beacon position
        if (!Hardware.isSet()) return;// TODO: 29.9.16 show warning to user about no hardware set
        Model model = Hardware.get().getModel();
        RangeScanner rangeScanner = model.getRangeScanner();
        RotatingRangeScanner scanner = rangeScanner instanceof RotatingRangeScanner
                ? (RotatingRangeScanner) rangeScanner : null;
        DistanceRangeFinder rangeFinder = model.getDistanceRangeFinder();
        MovePilot pilot = model.getPilot();
        //TouchDetector touch = hardware.getTouch();// TODO: 21.9.16 add detection and mapping
        if (pilot == null || rangeFinder == null)
            return;// TODO: 29.9.16 show warning to user about unsupported hardware

        Range allowedScanRange = null;
        if (scanner != null) {
            scanner.rotateTo(0);
            float[] angles = scanner.getAngles();
            allowedScanRange = new Range(angles[0], angles[angles.length - 1]);
        }

        while (!Thread.interrupted()) {
            SeekResult result = rangeFinder.fetchSeek();

            if (result == null || result.getDistance() == Float.POSITIVE_INFINITY) {
                pilot.stop();
                Thread.yield();
                continue;
            }

            float beaconPos = result.getBeaconPosAngle();
            if (scanner != null) {
                float scannerTacho = scanner.getTachoCount();
                if (beaconPos > 0 && scannerTacho > allowedScanRange.getHigher()
                        || beaconPos < 0 && scannerTacho < allowedScanRange.getLower()) {
                    scanner.stop();
                } else {
                    scanner.setSpeed((int) beaconPos);
                    scanner.forward();
                }
                beaconPos += scannerTacho;
            }

            if (result.getDistance() < 15f) {
                pilot.stop();
                Thread.yield();
                continue;
            }

            pilot.arcForward(beaconPos);
            Thread.yield();
        }

        pilot.stop();
        if (scanner != null) scanner.rotateTo(0);
    }

    public static final class BeaconFollowMode implements MenuItem {

        @Override
        public String getName() {
            return "BeaconFollow";
        }

        @Override
        public boolean onSelected(Menu menu, int itemIndex) {
            Canvas canvas = menu.generateSubmenuCanvas(itemIndex, Menu.calculateMinMenuHeight(5));
            canvas.apply();
            BeaconFollow beaconFollow = new BeaconFollow();
            beaconFollow.setCanvas(canvas, true);
            beaconFollow.start();
            return true;
        }
    }
}
