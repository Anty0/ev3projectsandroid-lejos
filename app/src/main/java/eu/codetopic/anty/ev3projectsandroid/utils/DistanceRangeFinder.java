package eu.codetopic.anty.ev3projectsandroid.utils;

import android.support.annotation.Nullable;

import eu.codetopic.utils.log.Log;
import lejos.hardware.device.DeviceIdentifier;
import lejos.hardware.port.Port;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.robotics.RangeFinder;
import lejos.robotics.SampleProvider;

public class DistanceRangeFinder implements RangeFinder {

    private static final String LOG_TAG = "DistanceRangeFinder";

    private static final float MAX_VALUE_DISTANCE_EV3_IR = 50f;
    private static final float MAX_VALUE_DISTANCE_EV3_ULTRASONIC = 255f;
    private static final float MAX_VALUE_DISTANCE_NXT_ULTRASONIC = 255f;

    private static final float CM_MUL_DISTANCE_EV3_IR = 1f;
    private static final float CM_MUL_DISTANCE_EV3_ULTRASONIC = 100f;
    private static final float CM_MUL_DISTANCE_NXT_ULTRASONIC = 100f;

    private static final String SENSOR_NAME_DISTANCE_EV3_IR = "lejos.hardware.sensor.EV3IRSensor";
    private static final String SENSOR_NAME_DISTANCE_EV3_ULTRASONIC = "lejos.hardware.sensor.EV3UltrasonicSensor";
    private static final String SENSOR_NAME_DISTANCE_NXT_ULTRASONIC = "lejos.hardware.sensor.NXTUltrasonicSensor";

    private final float[] distanceBuf;
    private final float[] seekBuf;

    private final DistanceSensorType sensorType;
    private final SampleProvider distanceProvider;
    @Nullable private final SampleProvider seekProvider;

    public DistanceRangeFinder(RemoteRequestEV3 ev3, String portName) {
        this(ev3, portName, null);
    }

    public DistanceRangeFinder(RemoteRequestEV3 ev3, String portName, @Nullable DistanceSensorType type) {
        this.sensorType = type == null ? DistanceSensorType.detectType(ev3, portName) : type;
        this.distanceProvider = sensorType.createDistanceProvider(ev3, portName);
        this.seekProvider = sensorType.createSeekProvider(ev3, portName);

        this.distanceBuf = new float[distanceProvider.sampleSize()];
        this.seekBuf = seekProvider != null ? new float[seekProvider.sampleSize()] : null;
    }

    public DistanceSensorType getSensorType() {
        return sensorType;
    }

    public float fetchDistance() {
        synchronized (distanceBuf) {
            distanceProvider.fetchSample(distanceBuf, 0);
            return sensorType.convertToCm(distanceBuf[0]);
        }
    }

    public SampleProvider getDistanceProvider() {
        return distanceProvider;
    }

    @Nullable
    public SeekResult fetchSeek() {
        if (seekProvider == null) return null;

        synchronized (seekBuf) {
            seekProvider.fetchSample(seekBuf, 0);

            int distanceIndex = 1;
            while (distanceIndex < seekBuf.length && seekBuf[distanceIndex] == Float.POSITIVE_INFINITY) {
                distanceIndex += 2;
            }

            return distanceIndex >= seekBuf.length ? null : new SeekResult((distanceIndex - 1) / 2,
                    seekBuf[distanceIndex - 1], seekBuf[distanceIndex]);
        }
    }

    public boolean hasSeek() {
        return seekProvider != null;
    }

    @Nullable
    public SampleProvider getSeekProvider() {
        return seekProvider;
    }

    @Override
    public float getRange() {
        return fetchDistance();
    }

    @Override
    public float[] getRanges() {
        return new float[]{getRange()};
    }

    public enum DistanceSensorType {
        EV3_IR(SENSOR_NAME_DISTANCE_EV3_IR, MAX_VALUE_DISTANCE_EV3_IR, CM_MUL_DISTANCE_EV3_IR),
        EV3_ULTRASONIC(SENSOR_NAME_DISTANCE_EV3_ULTRASONIC, MAX_VALUE_DISTANCE_EV3_ULTRASONIC, CM_MUL_DISTANCE_EV3_ULTRASONIC),
        NXT_ULTRASONIC(SENSOR_NAME_DISTANCE_NXT_ULTRASONIC, MAX_VALUE_DISTANCE_NXT_ULTRASONIC, CM_MUL_DISTANCE_NXT_ULTRASONIC),
        UNKNOWN("", 0f, 0f);

        private final String sensorName;
        private final float distanceMaxValue;
        private final float convertMul;

        DistanceSensorType(String sensorName, float distanceMaxValue, float convertMul) {
            this.sensorName = sensorName;
            this.distanceMaxValue = distanceMaxValue;
            this.convertMul = convertMul;
        }

        public static DistanceSensorType detectType(RemoteRequestEV3 ev3, String portName) {
            return detectType(ev3.getPort(portName));
        }

        public static DistanceSensorType detectType(Port port) {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier(port);
            String signature = deviceIdentifier.getDeviceSignature(false);
            deviceIdentifier.close();

            if (signature.equals("UART:IR-PROX")) return EV3_IR;
            if (signature.equals("UART:US-DIST-CM")) return EV3_ULTRASONIC;
            if (signature.contains("LEGO/SONAR")) return NXT_ULTRASONIC;

            Log.d(LOG_TAG, "Can't detect sensor signature: " + signature);
            return UNKNOWN;
        }

        public float getDistanceMaxValue() {
            return distanceMaxValue;
        }

        private float convertToCm(float distance) {
            return distance * convertMul;
        }

        private SampleProvider createDistanceProvider(RemoteRequestEV3 ev3, String portName) {
            return ev3.createSampleProvider(portName, sensorName, "Distance");
        }

        @Nullable
        private SampleProvider createSeekProvider(RemoteRequestEV3 ev3, String portName) {
            return this == EV3_IR ? ev3.createSampleProvider(portName, sensorName, "Seek") : null;
        }
    }

    public static final class SeekResult {

        private final int channel;
        private final float beaconPosAngle;
        private final float distance;

        private SeekResult(int channel, float beaconPosAngle, float distance) {
            this.channel = channel;
            this.beaconPosAngle = beaconPosAngle;
            this.distance = distance;
        }

        public int getChannel() {
            return channel;
        }

        public float getBeaconPosAngle() {
            return beaconPosAngle;
        }

        public float getDistance() {
            return distance;
        }

        @Override
        public String toString() {
            return "SeekResult{" +
                    "distance=" + distance +
                    ", beaconPosAngle=" + beaconPosAngle +
                    ", channel=" + channel +
                    '}';
        }
    }
}
