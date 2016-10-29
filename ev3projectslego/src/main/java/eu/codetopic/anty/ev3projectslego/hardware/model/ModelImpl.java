package eu.codetopic.anty.ev3projectslego.hardware.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;

import eu.codetopic.anty.ev3projectsbase.ModelInfo;
import eu.codetopic.anty.ev3projectslego.utils.motor.RegulatedMotorGearRatioWrapper;
import eu.codetopic.anty.ev3projectslego.utils.scan.FixedRangeScanner;
import eu.codetopic.anty.ev3projectslego.utils.scan.RotatingRangeScanner;
import eu.codetopic.anty.ev3projectslego.utils.scan.ScannerImpl;
import eu.codetopic.anty.ev3projectslego.utils.scan.base.RangeScanner;
import eu.codetopic.anty.ev3projectslego.utils.scan.base.Scanner;
import lejos.hardware.ev3.EV3;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class ModelImpl extends Model {

    private static final String LOG_TAG = "ModelImpl";

    private final ModelInfo modelInfo;

    @Nullable private RegulatedMotor leftMotor;
    @Nullable private RegulatedMotor rightMotor;
    @Nullable private Chassis chassis;
    @Nullable private MovePilot pilot;

    @Nullable private ScannerImpl scanner;
    @Nullable private RangeScanner rangeScanner;

    public ModelImpl(@NotNull ModelInfo modelInfo) {
        this.modelInfo = modelInfo;
    }

    @NotNull
    @Override
    public String getName() {
        return modelInfo.name;
    }

    @Override
    public void initialize(EV3 ev3) {
        ModelInfo.WheelsInfo wheels = modelInfo.wheelsInfo;
        if (wheels != null) {
            Wheel leftWheel = ModelInfoUtils.createWheel(ev3, wheels.leftWheel);
            Wheel rightWheel = ModelInfoUtils.createWheel(ev3, wheels.rightWheel);
            leftMotor = leftWheel.getMotor();
            rightMotor = rightWheel.getMotor();
            chassis = new WheeledChassis(new Wheel[]{leftWheel, rightWheel},
                    WheeledChassis.TYPE_DIFFERENTIAL);
            pilot = new MovePilot(chassis);
        } else pilot = null;

        ModelInfo.ScannerInfo scanner = modelInfo.scannerInfo;
        if (scanner != null) {
            this.scanner = new ScannerImpl(ev3.getPort(scanner.sensorDistancePort),
                    ScannerImpl.ScannerType.fromModel(scanner.sensorType));

            if (scanner instanceof ModelInfo.RotatingScannerInfo) {
                ModelInfo.RotatingScannerInfo rotatingScanner = (ModelInfo.RotatingScannerInfo) scanner;

                rangeScanner = new RotatingRangeScanner(this.scanner, rotatingScanner.scannerOffsetX,
                        rotatingScanner.scannerOffsetY, rotatingScanner.rotatingOffsetY,
                        new RegulatedMotorGearRatioWrapper(ModelInfoUtils
                                .createMotor(ev3.getPort(rotatingScanner.motorHeadPort),
                                        rotatingScanner.motorHeadType),
                                rotatingScanner.motorHeadGearRatio),
                        rotatingScanner.scanningAngleFrom, rotatingScanner.scanningAngleTo);
            } else {
                if (pilot != null) {
                    rangeScanner = new FixedRangeScanner(this.scanner, scanner.rotatingOffsetY, pilot);
                } else {
                    rangeScanner = null;
                }
            }
        } else {
            this.scanner = null;
            rangeScanner = null;
        }

        // TODO: 28.9.16 add support for TouchSensor
        // TODO: 28.9.16 add support for ColorSensor
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    @Nullable
    @Override
    public RegulatedMotor getMotor(MotorPosition position) {
        switch (position) {
            case WHEEL_LEFT:
                return leftMotor;
            case WHEEL_RIGHT:
                return rightMotor;
        }
        return null;
    }

    @Nullable
    @Override
    public Chassis getChassis() {
        return chassis;
    }

    @Override
    @Nullable
    public MovePilot getPilot() {
        return pilot;
    }

    @Override
    @Nullable
    public Scanner getScanner() {
        return scanner;
    }

    @Override
    @Nullable
    public RangeScanner getRangeScanner() {
        return rangeScanner;
    }

    @Override
    public void close() throws IOException {
        pilot = null;
        chassis = null;
        if (leftMotor != null) {
            leftMotor.close();
            leftMotor = null;
        }
        if (rightMotor != null) {
            rightMotor.close();
            rightMotor = null;
        }

        if (rangeScanner instanceof Closeable) {
            ((Closeable) rangeScanner).close();
            rangeScanner = null;
        }
        if (scanner != null) {
            scanner.close();
            scanner = null;
        }
    }
}
