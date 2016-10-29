package eu.codetopic.anty.ev3projectsbase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public class ModelInfo implements Serializable {

    private static final String LOG_TAG = "ModelInfo";

    @NotNull public String name;
    @Nullable public WheelsInfo wheelsInfo;
    @Nullable public ScannerInfo scannerInfo;
    @Nullable public TouchInfo touchInfo;
    @Nullable public ColorInfo colorInfo;

    public ModelInfo(@NotNull String name, @Nullable WheelsInfo wheelsInfo,
                     @Nullable ScannerInfo scannerInfo, @Nullable TouchInfo touchInfo,
                     @Nullable ColorInfo colorInfo) {

        this.name = name;
        this.wheelsInfo = wheelsInfo;
        this.scannerInfo = scannerInfo;
        this.touchInfo = touchInfo;
        this.colorInfo = colorInfo;
    }

    public enum MotorType {
        EV3_LARGE, EV3_MEDIUM, NXT_LARGE
    }

    public enum ScannerSensorType {
        EV3_IR, EV3_ULTRASONIC, NXT_ULTRASONIC, UNKNOWN
    }

    public static class WheelsInfo implements Serializable {

        public WheelInfo leftWheel;
        public WheelInfo rightWheel;

        public WheelsInfo() {
        }

        public WheelsInfo(WheelInfo leftWheel, WheelInfo rightWheel) {
            this.leftWheel = leftWheel;
            this.rightWheel = rightWheel;
        }
    }

    public static class WheelInfo implements Serializable {

        public MotorType motorType;
        public String port;
        public double offset;
        public double diameter;
        public double gearRatio;
        public boolean invert;

        public WheelInfo() {
        }

        public WheelInfo(MotorType motorType, String port, double offset, double diameter, double gearRatio, boolean invert) {
            this.motorType = motorType;
            this.port = port;
            this.offset = offset;
            this.diameter = diameter;
            this.gearRatio = gearRatio;
            this.invert = invert;
        }
    }

    public static class ScannerInfo implements Serializable {

        public String sensorDistancePort = "S4";
        @NotNull public ScannerSensorType sensorType = ScannerSensorType.UNKNOWN;
        public float rotatingOffsetY = 0f;

        public ScannerInfo() {
        }

        public ScannerInfo(String sensorDistancePort, @NotNull ScannerSensorType sensorType, float rotatingOffsetY) {
            this.sensorDistancePort = sensorDistancePort;
            this.sensorType = sensorType;
            this.rotatingOffsetY = rotatingOffsetY;
        }
    }

    public static class RotatingScannerInfo extends ScannerInfo {

        public String motorHeadPort = "A";
        public MotorType motorHeadType = MotorType.EV3_MEDIUM;
        public float scannerOffsetX = 0f;
        public float scannerOffsetY = 0f;
        public float motorHeadGearRatio = 1f;
        public int scanningAngleFrom = 0;
        public int scanningAngleTo = 360;

        public RotatingScannerInfo() {
        }

        public RotatingScannerInfo(String sensorDistancePort, @NotNull ScannerSensorType sensorType,
                                   float rotatingOffsetY, float scannerOffsetX, float scannerOffsetY,
                                   String motorHeadPort, MotorType motorHeadType, float motorHeadGearRatio,
                                   int scanningAngleFrom, int scanningAngleTo) {
            super(sensorDistancePort, sensorType, rotatingOffsetY);
            this.scannerOffsetX = scannerOffsetX;
            this.scannerOffsetY = scannerOffsetY;
            this.sensorDistancePort = sensorDistancePort;
            this.sensorType = sensorType;
            this.motorHeadPort = motorHeadPort;
            this.motorHeadType = motorHeadType;
            this.motorHeadGearRatio = motorHeadGearRatio;
            this.scanningAngleFrom = scanningAngleFrom;
            this.scanningAngleTo = scanningAngleTo;
        }
    }

    public static class TouchInfo implements Serializable {

        public String sensorPort = "S1";
        public double xOffset = 0d;
        public double yOffset = 0d;

        public TouchInfo() {
        }

        public TouchInfo(String sensorPort, double xOffset, double yOffset) {
            this.sensorPort = sensorPort;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }

    public static class ColorInfo implements Serializable {

        public String sensorPort = "S1";
        public double xOffset = 0d;
        public double yOffset = 0d;

        public ColorInfo() {
        }

        public ColorInfo(String sensorPort, double xOffset, double yOffset) {
            this.sensorPort = sensorPort;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }

}
