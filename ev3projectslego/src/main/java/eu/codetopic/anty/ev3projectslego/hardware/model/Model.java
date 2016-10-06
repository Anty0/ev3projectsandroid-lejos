package eu.codetopic.anty.ev3projectslego.hardware.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;

import eu.codetopic.anty.ev3projectslego.utils.scan.DistanceRangeFinder;
import lejos.hardware.ev3.EV3;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.navigation.MovePilot;

public abstract class Model implements Closeable {

    public static final double DEG_TO_RAD_MUL = Math.PI / 180d;

    @NotNull
    public abstract String getName();

    public abstract void initialize(EV3 ev3);

    @Nullable
    public abstract RegulatedMotor getMotor(MotorPosition position);

    @Nullable
    public abstract Chassis getChassis();

    @Nullable
    public abstract MovePilot getPilot();

    @Nullable
    public abstract DistanceRangeFinder getDistanceRangeFinder();

    @Nullable
    public abstract RangeScanner getRangeScanner();

    public enum MotorPosition {
        WHEEL_LEFT, WHEEL_RIGHT, SCANNER_HEAD
    }
}
