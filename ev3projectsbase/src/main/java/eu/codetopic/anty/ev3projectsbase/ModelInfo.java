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

    public enum DistanceSensorType {
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
        @Nullable public DistanceSensorType sensorType = null;

        public ScannerInfo() {
        }

        public ScannerInfo(String sensorDistancePort, @Nullable DistanceSensorType sensorType) {
            this.sensorDistancePort = sensorDistancePort;
            this.sensorType = sensorType;
        }
    }

    public static class RotatingScannerInfo extends ScannerInfo {

        public String motorHeadPort = "A";
        public MotorType motorHeadType = MotorType.EV3_MEDIUM;
        public double motorHeadGearRatio = 1d;
        public float motorHeadAngleMin = -90f;
        public float motorHeadAngleMax = 90f;

        public RotatingScannerInfo() {
        }

        public RotatingScannerInfo(String sensorDistancePort, @Nullable DistanceSensorType sensorType,
                                   String motorHeadPort, MotorType motorHeadType, double motorHeadGearRatio,
                                   float motorHeadAngleMin, float motorHeadAngleMax) {
            super(sensorDistancePort, sensorType);
            this.sensorDistancePort = sensorDistancePort;
            this.sensorType = sensorType;
            this.motorHeadPort = motorHeadPort;
            this.motorHeadType = motorHeadType;
            this.motorHeadGearRatio = motorHeadGearRatio;
            this.motorHeadAngleMin = motorHeadAngleMin;
            this.motorHeadAngleMax = motorHeadAngleMax;
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
