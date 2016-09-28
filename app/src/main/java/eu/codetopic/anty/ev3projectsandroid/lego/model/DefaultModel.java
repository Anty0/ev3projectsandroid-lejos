package eu.codetopic.anty.ev3projectsandroid.lego.model;

import static eu.codetopic.anty.ev3projectsandroid.lego.model.ModelInfo.ColorInfo;
import static eu.codetopic.anty.ev3projectsandroid.lego.model.ModelInfo.MotorType;
import static eu.codetopic.anty.ev3projectsandroid.lego.model.ModelInfo.RotatingScannerInfo;
import static eu.codetopic.anty.ev3projectsandroid.lego.model.ModelInfo.TouchInfo;
import static eu.codetopic.anty.ev3projectsandroid.lego.model.ModelInfo.WheelsInfo;

public class DefaultModel extends BaseModel {// TODO: 28.9.16 remake default model as constant implementation of user-created model

    private static final String LOG_TAG = "DefaultModel";

    private static final String WHEEL_LEFT_PORT = "B";
    private static final String WHEEL_RIGHT_PORT = "C";
    private static final double WHEELS_DIAMETER = 4.3d;
    private static final double WHEELS_TRACK_WIDTH = 10.4d;

    private static final String SENSOR_DISTANCE_PORT = "S4";
    private static final String MOTOR_SCANNER_HEAD_PORT = "A";
    private static final MotorType MOTOR_SCANNER_HEAD_TYPE = MotorType.EV3_MEDIUM;
    private static final double SCANNER_GEAR_RATIO = 20d / 12d;
    private static final float MOTOR_SCANNER_HEAD_ANGLE_MIN = -90;
    private static final float MOTOR_SCANNER_HEAD_ANGLE_MAX = 90;

    private static final String SENSOR_TOUCH_PORT = "S1";
    private static final double SENSOR_TOUCH_OFFSET_X = 0d;
    private static final double SENSOR_TOUCH_OFFSET_Y = 3.5d;

    private static final String SENSOR_COLOR_PORT = "S3";
    private static final double SENSOR_COLOR_OFFSET_X = 0d;
    private static final double SENSOR_COLOR_OFFSET_Y = 3.5d;

    public DefaultModel() {
        super(new ModelInfo("Project Default model",
                new WheelsInfo(WHEEL_LEFT_PORT, WHEEL_RIGHT_PORT, WHEELS_DIAMETER, WHEELS_TRACK_WIDTH),
                new RotatingScannerInfo(SENSOR_DISTANCE_PORT, null,
                        MOTOR_SCANNER_HEAD_PORT, MOTOR_SCANNER_HEAD_TYPE, SCANNER_GEAR_RATIO,
                        MOTOR_SCANNER_HEAD_ANGLE_MIN, MOTOR_SCANNER_HEAD_ANGLE_MAX),
                new TouchInfo(SENSOR_TOUCH_PORT, SENSOR_TOUCH_OFFSET_X, SENSOR_TOUCH_OFFSET_Y),
                new ColorInfo(SENSOR_COLOR_PORT, SENSOR_COLOR_OFFSET_X, SENSOR_COLOR_OFFSET_Y)));
    }
}
