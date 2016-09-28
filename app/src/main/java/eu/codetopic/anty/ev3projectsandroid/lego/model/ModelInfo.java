package eu.codetopic.anty.ev3projectsandroid.lego.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

import eu.codetopic.anty.ev3projectsandroid.utils.DistanceRangeFinder.DistanceSensorType;

public class ModelInfo implements Serializable {

    private static final String LOG_TAG = "ModelInfo";

    @NonNull public String name;
    @Nullable public WheelsInfo wheelsInfo;
    @Nullable public ScannerInfo scannerInfo;
    @Nullable public TouchInfo touchInfo;
    @Nullable public ColorInfo colorInfo;

    public ModelInfo(@NonNull String name, @Nullable WheelsInfo wheelsInfo,
                     @Nullable ScannerInfo scannerInfo, @Nullable TouchInfo touchInfo,
                     @Nullable ColorInfo colorInfo) {

        this.name = name;
        this.wheelsInfo = wheelsInfo;
        this.scannerInfo = scannerInfo;
        this.touchInfo = touchInfo;
        this.colorInfo = colorInfo;
    }

    public enum MotorType {
        EV3_LARGE('L'), EV3_MEDIUM('M'), NXT_LARGE('N'), GLIDE('G');

        private final char typeId;

        MotorType(char typeId) {
            this.typeId = typeId;
        }

        public char getTypeId() {
            return typeId;
        }
    }

    public static class WheelsInfo implements Serializable {

        public String leftPort = "B";
        public String rightPort = "C";
        public double diameter;
        public double trackWidth;

        public WheelsInfo() {
        }

        public WheelsInfo(String leftPort, String rightPort, double diameter, double trackWidth) {
            this.leftPort = leftPort;
            this.rightPort = rightPort;
            this.diameter = diameter;
            this.trackWidth = trackWidth;
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
