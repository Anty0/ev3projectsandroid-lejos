package eu.codetopic.anty.ev3projectslego.mode.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.RMISlamMode;
import eu.codetopic.anty.ev3projectsbase.slam.base.move.Move;
import eu.codetopic.anty.ev3projectsbase.slam.base.move.RotateMove;
import eu.codetopic.anty.ev3projectsbase.slam.base.move.TravelMove;
import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;
import eu.codetopic.anty.ev3projectslego.hardware.Hardware;
import eu.codetopic.anty.ev3projectslego.hardware.model.Model;
import eu.codetopic.anty.ev3projectslego.mode.ModeController;
import eu.codetopic.anty.ev3projectslego.utils.Utils;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import eu.codetopic.anty.ev3projectslego.utils.scan.base.RangeScanner;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.localization.PoseProvider;
import lejos.utility.Delay;

public class SlamClientMode extends ModeController implements RMISlamMode {

    private static final String LOG_TAG = "SlamClientMode";

    private volatile boolean run = false;
    private Chassis chassis = null;
    private PoseProvider odometry = null;
    private RangeScanner rangeScanner = null;

    @Override
    public Class<?> getRmiInterface() {
        return RMISlamMode.class;
    }

    @Override
    public boolean isSupported() {
        if (!Hardware.isSet()) return false;
        Model model = Hardware.get().getModel();
        return model.getRangeScanner() != null
                && model.getChassis() != null;
    }

    @Override
    protected void onStart(@Nullable Canvas canvas) {
        Model model = Hardware.get().getModel();
        chassis = model.getChassis();
        //noinspection ConstantConditions
        odometry = chassis.getPoseProvider();
        rangeScanner = model.getRangeScanner();

        run = true;

        while (run) Thread.yield();

        chassis = null;
        odometry = null;
        rangeScanner = null;
    }

    @Override
    public Pose getOdometryPose() {
        validateRun();
        lejos.robotics.navigation.Pose pose = odometry.getPose();
        return new Pose(pose.getX(), pose.getY(), pose.getHeading());
    }

    @Override
    public void setOdometryPose(@NotNull Pose pose) {
        validateRun();
        odometry.setPose(new lejos.robotics.navigation
                .Pose(pose.getX(), pose.getY(), pose.getHeading()));
    }

    @Override
    public void move(@NotNull Move move) {
        validateRun();
        if (move instanceof TravelMove) {
            chassis.travel(((TravelMove) move).getDistance());
            return;
        }
        if (move instanceof RotateMove) {
            chassis.rotate(((RotateMove) move).getAngle());
            return;
        }
        Delay.msDelay(100);

        throw new IllegalArgumentException("Requested move is not usable, move: " + move);
    }

    @Override
    public ScanResults scan() {
        validateRun();
        return rangeScanner.aroundScan(RangeScanner.MOTOR_SCAN_SPEED_FAST);
    }

    @Override
    public void stop() {
        validateRun();
        run = false;
        Utils.waitWhile(this::isRunning);
    }

    private void validateRun() {
        if (!run) {
            if (isRunning()) {
                Utils.waitWhile(() -> isRunning() && !run);
                if (run) return;
            }
            throw new IllegalStateException(LOG_TAG + " is not running");
        }
    }
}
