package eu.codetopic.anty.ev3projectslego.utils.scan;

import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;

import eu.codetopic.anty.ev3projectsbase.ModelInfo;
import lejos.hardware.device.DeviceIdentifier;
import lejos.hardware.ev3.EV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.BaseSensor;
import lejos.robotics.RangeFinder;
import lejos.robotics.SampleProvider;

public class DistanceRangeFinder implements RangeFinder, Closeable {

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
    private final BaseSensor sensor;
    private final SampleProvider distanceProvider;
    @Nullable private final SampleProvider seekProvider;

    public DistanceRangeFinder(Port port) {
        this(port, null);
    }

    public DistanceRangeFinder(Port port, DistanceSensorType type) {
        this.sensorType = type == null || type == DistanceSensorType.UNKNOWN
                ? DistanceSensorType.detectType(port) : type;
        sensor = sensorType.createSensor(port);
        this.distanceProvider = sensorType.getDistanceProvider(sensor);
        this.seekProvider = sensorType.getSeekProvider(sensor);

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

    @Override
    public void close() throws IOException {
        sensor.close();
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

        public static DistanceSensorType detectType(EV3 ev3, String portName) {
            return detectType(ev3.getPort(portName));
        }

        public static DistanceSensorType detectType(Port port) {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier(port);
            String signature = deviceIdentifier.getDeviceSignature(false);
            deviceIdentifier.close();

            if (signature.equals("UART:IR-PROX")) return EV3_IR;
            if (signature.equals("UART:US-DIST-CM")) return EV3_ULTRASONIC;
            if (signature.contains("LEGO/SONAR")) return NXT_ULTRASONIC;

            System.err.println(LOG_TAG + ": Can't detect sensor signature: " + signature);
            return UNKNOWN;
        }

        public static DistanceSensorType fromModel(ModelInfo.DistanceSensorType modelSensorType) {
            return DistanceSensorType.valueOf(modelSensorType.name());
        }

        public float getDistanceMaxValue() {
            return distanceMaxValue;
        }

        public float convertToCm(float distance) {
            return distance * convertMul;
        }

        public BaseSensor createSensor(Port port) {
            try {
                return (BaseSensor) Class.forName(sensorName)
                        .getConstructor(Port.class).newInstance(port);
            } catch (Exception e) {
                System.err.println("Failed to create sensor " + sensorName);
                return null;
            }
        }

        public SampleProvider getDistanceProvider(BaseSensor sensor) {
            return sensor.getMode("Distance");
        }

        @Nullable
        public SampleProvider getSeekProvider(BaseSensor sensor) {
            return this == EV3_IR ? sensor.getMode("Seek") : null;
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
