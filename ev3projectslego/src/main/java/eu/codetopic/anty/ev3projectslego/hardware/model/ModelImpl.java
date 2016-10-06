package eu.codetopic.anty.ev3projectslego.hardware.model;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;

import eu.codetopic.anty.ev3projectsbase.ModelInfo;
import eu.codetopic.anty.ev3projectslego.utils.scan.DistanceRangeFinder;
import eu.codetopic.anty.ev3projectslego.utils.scan.FixedRangeScanner;
import eu.codetopic.anty.ev3projectslego.utils.scan.RotatingRangeScanner;
import lejos.hardware.ev3.EV3;
import lejos.robotics.RangeScanner;
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

    @Nullable private RegulatedMotor scannerMotor;
    @Nullable private DistanceRangeFinder rangeFinder;
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
            rangeFinder = new DistanceRangeFinder(ev3.getPort(scanner.sensorDistancePort),
                    DistanceRangeFinder.DistanceSensorType.fromModel(scanner.sensorType));

            if (scanner instanceof ModelInfo.RotatingScannerInfo) {
                ModelInfo.RotatingScannerInfo rotatingScanner = (ModelInfo.RotatingScannerInfo) scanner;

                scannerMotor = ModelInfoUtils.createMotor(ev3.getPort(rotatingScanner.motorHeadPort),
                        rotatingScanner.motorHeadType);
                rangeScanner = new RotatingRangeScanner(scannerMotor, rangeFinder,
                        rotatingScanner.motorHeadGearRatio);

                float angle = rotatingScanner.motorHeadAngleMin;
                ArrayList<Float> scanAngles = new ArrayList<>();
                while (angle <= rotatingScanner.motorHeadAngleMax) {
                    scanAngles.add(angle);
                    angle += 5f;
                }
                if (angle != rotatingScanner.motorHeadAngleMax)
                    scanAngles.add(rotatingScanner.motorHeadAngleMax);
                rangeScanner.setAngles(ArrayUtils.toPrimitive(
                        scanAngles.toArray(new Float[scanAngles.size()])));
            } else {
                scannerMotor = null;
                if (pilot != null) {
                    rangeScanner = new FixedRangeScanner(pilot, rangeFinder);

                    ArrayList<Float> scanAngles = new ArrayList<>();
                    for (float angle = 0; angle <= 360; angle += 60f) {
                        scanAngles.add(angle);
                    }
                    rangeScanner.setAngles(ArrayUtils.toPrimitive(
                            scanAngles.toArray(new Float[scanAngles.size()])));
                } else rangeScanner = null;
            }
        } else {
            scannerMotor = null;
            rangeFinder = null;
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
            case SCANNER_HEAD:
                return scannerMotor;
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
    public DistanceRangeFinder getDistanceRangeFinder() {
        return rangeFinder;
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

        rangeScanner = null;
        if (rangeFinder != null) {
            rangeFinder.close();
            rangeFinder = null;
        }
        if (scannerMotor != null) {
            scannerMotor.stop();
            scannerMotor.setSpeed((int) scannerMotor.getMaxSpeed());
            scannerMotor.rotateTo(0);
            scannerMotor.close();
            scannerMotor = null;
        }
    }
}
