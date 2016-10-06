package eu.codetopic.anty.ev3projectsbase;

import static eu.codetopic.anty.ev3projectsbase.ModelInfo.ColorInfo;
import static eu.codetopic.anty.ev3projectsbase.ModelInfo.MotorType;
import static eu.codetopic.anty.ev3projectsbase.ModelInfo.RotatingScannerInfo;
import static eu.codetopic.anty.ev3projectsbase.ModelInfo.TouchInfo;
import static eu.codetopic.anty.ev3projectsbase.ModelInfo.WheelInfo;
import static eu.codetopic.anty.ev3projectsbase.ModelInfo.WheelsInfo;

public final class DefaultModelInfo {

    private static final String LOG_TAG = "DefaultModelInfo";

    private static final MotorType WHEEL_LEFT_TYPE = MotorType.EV3_LARGE;
    private static final String WHEEL_LEFT_PORT = "B";
    private static final double WHEEL_LEFT_OFFSET = -5.2d;
    private static final MotorType WHEEL_RIGHT_TYPE = MotorType.EV3_LARGE;
    private static final String WHEEL_RIGHT_PORT = "C";
    private static final double WHEEL_RIGHT_OFFSET = 5.2d;
    private static final double WHEEL_DIAMETER = 4.3d;
    private static final double WHEEL_GEAR_RATIO = 1d;
    private static final boolean WHEEL_INVERT = false;

    private static final String SENSOR_DISTANCE_PORT = "S4";
    private static final String MOTOR_HEAD_PORT = "A";
    private static final MotorType MOTOR_HEAD_TYPE = MotorType.EV3_MEDIUM;
    private static final double SCANNER_GEAR_RATIO = 20d / 12d;
    private static final float MOTOR_SCANNER_HEAD_ANGLE_MIN = -90;
    private static final float MOTOR_SCANNER_HEAD_ANGLE_MAX = 90;

    private static final String SENSOR_TOUCH_PORT = "S1";
    private static final double SENSOR_TOUCH_OFFSET_X = 0d;
    private static final double SENSOR_TOUCH_OFFSET_Y = 3.5d;

    private static final String SENSOR_COLOR_PORT = "S3";
    private static final double SENSOR_COLOR_OFFSET_X = 0d;
    private static final double SENSOR_COLOR_OFFSET_Y = 3.5d;

    private DefaultModelInfo() {
    }

    public static ModelInfo getInstance() {
        return new ModelInfo("Project Default model",
                new WheelsInfo(
                        new WheelInfo(WHEEL_LEFT_TYPE, WHEEL_LEFT_PORT, WHEEL_LEFT_OFFSET,
                                WHEEL_DIAMETER, WHEEL_GEAR_RATIO, WHEEL_INVERT),
                        new WheelInfo(WHEEL_RIGHT_TYPE, WHEEL_RIGHT_PORT, WHEEL_RIGHT_OFFSET,
                                WHEEL_DIAMETER, WHEEL_GEAR_RATIO, WHEEL_INVERT)),
                new RotatingScannerInfo(SENSOR_DISTANCE_PORT, null,
                        MOTOR_HEAD_PORT, MOTOR_HEAD_TYPE, SCANNER_GEAR_RATIO,
                        MOTOR_SCANNER_HEAD_ANGLE_MIN, MOTOR_SCANNER_HEAD_ANGLE_MAX),
                new TouchInfo(SENSOR_TOUCH_PORT, SENSOR_TOUCH_OFFSET_X, SENSOR_TOUCH_OFFSET_Y),
                new ColorInfo(SENSOR_COLOR_PORT, SENSOR_COLOR_OFFSET_X, SENSOR_COLOR_OFFSET_Y));
    }
}
