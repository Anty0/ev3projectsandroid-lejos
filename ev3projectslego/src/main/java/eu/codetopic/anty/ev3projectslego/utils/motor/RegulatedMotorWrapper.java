package eu.codetopic.anty.ev3projectslego.utils.motor;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

public class RegulatedMotorWrapper extends MotorWrapper implements RegulatedMotor {

    private static final String LOG_TAG = "RegulatedMotorWrapper";

    protected final RegulatedMotor base;

    public RegulatedMotorWrapper(RegulatedMotor base) {
        super(base);
        this.base = getBase();
    }

    @Override
    public RegulatedMotor getBase() {
        return (RegulatedMotor) super.getBase();
    }

    @Override
    public void addListener(RegulatedMotorListener listener) {
        base.addListener(listener);
    }

    @Override
    public RegulatedMotorListener removeListener() {
        return base.removeListener();
    }

    @Override
    public void stop(boolean immediateReturn) {
        base.stop();
    }

    @Override
    public void flt(boolean immediateReturn) {
        base.flt();
    }

    @Override
    public void waitComplete() {
        base.waitComplete();
    }

    @Override
    public void rotate(int angle, boolean immediateReturn) {
        base.rotate(angle, immediateReturn);
    }

    @Override
    public void rotate(int angle) {
        base.rotate(angle);
    }

    @Override
    public void rotateTo(int limitAngle) {
        base.rotateTo(limitAngle);
    }

    @Override
    public void rotateTo(int limitAngle, boolean immediateReturn) {
        base.rotateTo(limitAngle, immediateReturn);
    }

    @Override
    public int getLimitAngle() {
        return base.getLimitAngle();
    }

    @Override
    public int getSpeed() {
        return base.getSpeed();
    }

    @Override
    public void setSpeed(int speed) {
        base.setSpeed(speed);
    }

    @Override
    public float getMaxSpeed() {
        return base.getMaxSpeed();
    }

    @Override
    public boolean isStalled() {
        return base.isStalled();
    }

    @Override
    public void setStallThreshold(int error, int time) {
        base.setStallThreshold(error, time);
    }

    @Override
    public void setAcceleration(int acceleration) {
        base.setAcceleration(acceleration);
    }

    @Override
    public void synchronizeWith(RegulatedMotor[] syncList) {
        base.synchronizeWith(syncList);
    }

    @Override
    public void startSynchronization() {
        base.startSynchronization();
    }

    @Override
    public void endSynchronization() {
        base.endSynchronization();
    }

    @Override
    public void close() {
        base.close();
    }

    @Override
    public int getRotationSpeed() {
        return base.getRotationSpeed();
    }

    @Override
    public int getTachoCount() {
        return base.getTachoCount();
    }

    @Override
    public void resetTachoCount() {
        base.resetTachoCount();
    }
}
