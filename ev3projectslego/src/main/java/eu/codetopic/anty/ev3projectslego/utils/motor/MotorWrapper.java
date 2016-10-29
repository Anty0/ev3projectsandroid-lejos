package eu.codetopic.anty.ev3projectslego.utils.motor;

import lejos.robotics.BaseMotor;

public class MotorWrapper implements BaseMotor {

    private static final String LOG_TAG = "MotorWrapper";

    protected final BaseMotor base;

    public MotorWrapper(BaseMotor base) {
        this.base = base;
    }

    public BaseMotor getBase() {
        return base;
    }

    @Override
    public void forward() {
        base.forward();
    }

    @Override
    public void backward() {
        base.backward();
    }

    @Override
    public void stop() {
        base.stop();
    }

    @Override
    public void flt() {
        base.flt();
    }

    @Override
    public boolean isMoving() {
        return base.isMoving();
    }
}
