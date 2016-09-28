package eu.codetopic.anty.ev3projectsandroid.lego.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;

import eu.codetopic.anty.ev3projectsandroid.utils.DistanceRangeFinder;
import eu.codetopic.anty.ev3projectsandroid.utils.FixedRangeScanner;
import eu.codetopic.anty.ev3projectsandroid.utils.RotatingRangeScanner;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.ArcRotateMoveController;

public abstract class BaseModel extends Model {

    private static final String LOG_TAG = "BaseModel";

    private final ModelInfo modelInfo;

    @Nullable private ArcRotateMoveController pilot;

    @Nullable private RegulatedMotor scannerMotor;
    @Nullable private DistanceRangeFinder rangeFinder;
    @Nullable private RangeScanner rangeScanner;

    public BaseModel(@NonNull ModelInfo modelInfo) {
        this.modelInfo = modelInfo;
    }

    @NonNull
    @Override
    public String getName() {
        return modelInfo.name;
    }

    @Override
    public void initialize(RemoteRequestEV3 ev3) throws IOException {
        ModelInfo.WheelsInfo wheels = modelInfo.wheelsInfo;
        if (wheels != null) {
            pilot = ev3.createPilot(wheels.diameter, wheels.trackWidth,
                    wheels.leftPort, wheels.rightPort);
        } else pilot = null;

        ModelInfo.ScannerInfo scanner = modelInfo.scannerInfo;
        if (scanner != null) {
            rangeFinder = new DistanceRangeFinder(ev3, scanner.sensorDistancePort, scanner.sensorType);

            if (scanner instanceof ModelInfo.RotatingScannerInfo) {
                ModelInfo.RotatingScannerInfo rotatingScanner = (ModelInfo.RotatingScannerInfo) scanner;

                scannerMotor = ev3.createRegulatedMotor(rotatingScanner.motorHeadPort,
                        rotatingScanner.motorHeadType.getTypeId());
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

    @Override
    @Nullable
    public ArcRotateMoveController getPilot() {
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

        rangeScanner = null;
        rangeFinder = null;
        if (scannerMotor != null) {
            scannerMotor.stop();
            scannerMotor.setSpeed((int) scannerMotor.getMaxSpeed());
            scannerMotor.rotateTo(0);
            scannerMotor.close();
            scannerMotor = null;
        }
    }
}
