package eu.codetopic.anty.ev3projectslego.utils.motor;

import lejos.robotics.RegulatedMotor;

public class RegulatedMotorGearRatioWrapper extends RegulatedMotorWrapper {

    private static final String LOG_TAG = "RegulatedMotorGearRatioWrapper";

    private final float gearRatio;

    public RegulatedMotorGearRatioWrapper(RegulatedMotor base, float gearRatio) {
        super(base);
        this.gearRatio = gearRatio;
    }

    @Override
    public void rotate(int angle, boolean immediateReturn) {
        super.rotate((int) (angle * gearRatio), immediateReturn);
    }

    @Override
    public void rotate(int angle) {
        super.rotate((int) (angle * gearRatio));
    }

    @Override
    public void rotateTo(int limitAngle) {
        super.rotateTo((int) (limitAngle * gearRatio));
    }

    @Override
    public void rotateTo(int limitAngle, boolean immediateReturn) {
        super.rotateTo((int) (limitAngle * gearRatio), immediateReturn);
    }

    @Override
    public int getLimitAngle() {
        return (int) (super.getLimitAngle() / gearRatio);
    }

    @Override
    public int getSpeed() {
        return (int) (super.getSpeed() / gearRatio);
    }

    @Override
    public void setSpeed(int speed) {
        super.setSpeed((int) (speed * gearRatio));
    }

    @Override
    public float getMaxSpeed() {
        return super.getMaxSpeed() / gearRatio;
    }

    @Override
    public void setAcceleration(int acceleration) {
        super.setAcceleration((int) (acceleration * gearRatio));
    }

    @Override
    public int getRotationSpeed() {
        return (int) (super.getRotationSpeed() / gearRatio);
    }

    @Override
    public int getTachoCount() {
        return (int) (super.getTachoCount() / gearRatio);
    }
}
