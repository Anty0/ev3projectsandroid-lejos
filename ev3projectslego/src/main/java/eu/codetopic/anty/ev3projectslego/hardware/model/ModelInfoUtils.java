package eu.codetopic.anty.ev3projectslego.hardware.model;

import eu.codetopic.anty.ev3projectsbase.ModelInfo.MotorType;
import eu.codetopic.anty.ev3projectsbase.ModelInfo.WheelInfo;
import lejos.hardware.ev3.EV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;

public final class ModelInfoUtils {

    private static final String LOG_TAG = "ModelInfoUtils";

    private ModelInfoUtils() {
    }


    public static RegulatedMotor createMotor(Port port, MotorType motorType) {
        switch (motorType) {
            case EV3_LARGE:
                return new EV3LargeRegulatedMotor(port);
            case EV3_MEDIUM:
                return new EV3MediumRegulatedMotor(port);
            case NXT_LARGE:
                return new NXTRegulatedMotor(port);
            default:
                return null;
        }
    }

    public static Wheel createWheel(EV3 ev3, WheelInfo wheelInfo) {
        return WheeledChassis.modelWheel(createMotor(ev3.getPort(wheelInfo.port),
                wheelInfo.motorType), wheelInfo.diameter).offset(wheelInfo.offset)
                .gearRatio(wheelInfo.gearRatio).invert(wheelInfo.invert);
    }

}
