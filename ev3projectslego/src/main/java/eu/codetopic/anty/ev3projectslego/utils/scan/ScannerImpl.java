package eu.codetopic.anty.ev3projectslego.utils.scan;

import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.codetopic.anty.ev3projectsbase.ModelInfo.ScannerSensorType;
import eu.codetopic.anty.ev3projectsbase.slam.base.scan.SeekResult;
import eu.codetopic.anty.ev3projectslego.utils.scan.base.Scanner;
import lejos.hardware.device.DeviceIdentifier;
import lejos.hardware.ev3.EV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.BaseSensor;
import lejos.robotics.SampleProvider;

public class ScannerImpl implements Scanner, Closeable {

    private static final String LOG_TAG = "ScannerImpl";

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

    private final ScannerType sensorType;
    private final BaseSensor sensor;
    private final SampleProvider distanceProvider;
    @Nullable private final SampleProvider seekProvider;

    public ScannerImpl(Port port) {
        this(port, null);
    }

    public ScannerImpl(Port port, ScannerType type) {
        this.sensorType = type == null || type == ScannerType.UNKNOWN
                ? ScannerType.detectType(port) : type;
        sensor = sensorType.createSensor(port);
        this.distanceProvider = sensorType.getDistanceProvider(sensor);
        this.seekProvider = sensorType.getSeekProvider(sensor);

        this.distanceBuf = new float[distanceProvider.sampleSize()];
        this.seekBuf = seekProvider != null ? new float[seekProvider.sampleSize()] : null;
    }

    public ScannerType getSensorType() {
        return sensorType;
    }

    @Override
    public float getMaxDistance() {
        return sensorType.getDistanceMaxValue();
    }

    public SampleProvider getDistanceProvider() {
        return distanceProvider;
    }

    public float fetchDistance() {
        synchronized (distanceBuf) {
            distanceProvider.fetchSample(distanceBuf, 0);
            return sensorType.convertToCm(distanceBuf[0]);
        }
    }

    public boolean hasSeek() {
        return seekProvider != null;
    }

    @Nullable
    public SampleProvider getSeekProvider() {
        return seekProvider;
    }

    @Nullable
    public SeekResult fetchSeek() {
        List<SeekResult> results = fetchAllSeek();
        return results.size() > 0 ? results.get(0) : null;
    }

    @Override
    public List<SeekResult> fetchAllSeek() {
        if (seekProvider == null) return Collections.emptyList();

        List<SeekResult> results = new ArrayList<>(3);
        synchronized (seekBuf) {
            seekProvider.fetchSample(seekBuf, 0);

            for (int i = 1, len = seekBuf.length; i < len; i += 2) {
                if (seekBuf[i] != Float.POSITIVE_INFINITY) {
                    results.add(new SeekResult((i - 1) / 2, seekBuf[i - 1], seekBuf[i]));
                }
            }
        }
        return results;
    }

    @Override
    public void close() throws IOException {
        sensor.close();
    }

    public enum ScannerType {
        EV3_IR(SENSOR_NAME_DISTANCE_EV3_IR, MAX_VALUE_DISTANCE_EV3_IR, CM_MUL_DISTANCE_EV3_IR),
        EV3_ULTRASONIC(SENSOR_NAME_DISTANCE_EV3_ULTRASONIC, MAX_VALUE_DISTANCE_EV3_ULTRASONIC, CM_MUL_DISTANCE_EV3_ULTRASONIC),
        NXT_ULTRASONIC(SENSOR_NAME_DISTANCE_NXT_ULTRASONIC, MAX_VALUE_DISTANCE_NXT_ULTRASONIC, CM_MUL_DISTANCE_NXT_ULTRASONIC),
        UNKNOWN("", 0f, 0f);

        private final String sensorName;
        private final float distanceMaxValue;
        private final float convertMul;

        ScannerType(String sensorName, float distanceMaxValue, float convertMul) {
            this.sensorName = sensorName;
            this.distanceMaxValue = distanceMaxValue;
            this.convertMul = convertMul;
        }

        public static ScannerType detectType(EV3 ev3, String portName) {
            return detectType(ev3.getPort(portName));
        }

        public static ScannerType detectType(Port port) {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier(port);
            String signature = deviceIdentifier.getDeviceSignature(false);
            deviceIdentifier.close();

            if (signature.equals("UART:IR-PROX")) return EV3_IR;
            if (signature.equals("UART:US-DIST-CM")) return EV3_ULTRASONIC;
            if (signature.contains("LEGO/SONAR")) return NXT_ULTRASONIC;

            System.err.println(LOG_TAG + ": Can't detect sensor signature: " + signature);
            return UNKNOWN;
        }

        public static ScannerType fromModel(ScannerSensorType modelSensorType) {
            return ScannerType.valueOf(modelSensorType.name());
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
}
