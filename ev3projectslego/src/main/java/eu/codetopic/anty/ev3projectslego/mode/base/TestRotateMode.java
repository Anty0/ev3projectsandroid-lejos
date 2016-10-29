package eu.codetopic.anty.ev3projectslego.mode.base;

import org.jetbrains.annotations.Nullable;

import eu.codetopic.anty.ev3projectslego.hardware.Hardware;
import eu.codetopic.anty.ev3projectslego.mode.ModeController;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import lejos.robotics.navigation.MovePilot;

public final class TestRotateMode extends ModeController {

    private static final String LOG_TAG = "TestRotateMode";

    @Override
    public boolean isSupported() {
        return Hardware.isSet() && Hardware.get().getModel().getPilot() != null;
    }

    @Override
    protected void onStart(@Nullable Canvas canvas) {
        MovePilot pilot = Hardware.get().getModel().getPilot();
        if (pilot == null) return;
        pilot.rotate(10 * 360);
    }
}
