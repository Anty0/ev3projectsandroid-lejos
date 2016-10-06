package eu.codetopic.anty.ev3projectslego.menu.base;

import eu.codetopic.anty.ev3projectslego.hardware.Hardware;
import eu.codetopic.anty.ev3projectslego.menu.BaseMode;
import lejos.robotics.navigation.MovePilot;

public final class TestForward extends BaseMode {

    private static final String LOG_TAG = "TestForward";

    public static boolean isSupported() {
        return Hardware.isSet() && Hardware.get().getModel().getPilot() != null;
    }

    @Override
    public void run() {
        MovePilot pilot = Hardware.get().getModel().getPilot();
        if (pilot == null) return;
        pilot.travel(50, true);
    }
}
