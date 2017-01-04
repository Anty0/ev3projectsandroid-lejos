package eu.codetopic.anty.ev3projectslego.mode.base;

import org.jetbrains.annotations.Nullable;

import eu.codetopic.anty.ev3projectslego.hardware.Hardware;
import eu.codetopic.anty.ev3projectslego.mode.ModeController;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;

public class LineFollowMode extends ModeController {

    private static final String LOG_TAG = "LineFollowMode";

    @Override
    public boolean isSupported() {
        return Hardware.isSet();// TODO: 4.1.17 has wheels and has color sensor
    }

    @Override
    protected void onStart(@Nullable Canvas canvas) {
        // TODO: 4.1.17 return if hasn't wheels or hasn't color sensor
        // TODO: 4.1.17 implement
    }

}
