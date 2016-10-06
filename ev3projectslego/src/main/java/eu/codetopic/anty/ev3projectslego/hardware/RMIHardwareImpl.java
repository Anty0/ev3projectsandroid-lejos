package eu.codetopic.anty.ev3projectslego.hardware;

import org.jetbrains.annotations.Nullable;

import eu.codetopic.anty.ev3projectsbase.ModelInfo;
import eu.codetopic.anty.ev3projectsbase.RMIHardware;
import eu.codetopic.anty.ev3projectslego.hardware.model.ModelImpl;
import eu.codetopic.anty.ev3projectslego.utils.looper.PostJob;

public class RMIHardwareImpl extends PostJob implements RMIHardware {

    private static final String LOG_TAG = "RMIHardwareImpl";

    @Override
    public void setup(@Nullable ModelInfo model) {
        postJob(() -> Hardware.setup(model == null ? null : new ModelImpl(model)), true);
    }

    @Override
    public boolean isSet() {
        return Hardware.isSet();
    }

    @Override
    public String getActiveModelName() {
        Hardware hardware = Hardware.get();
        return hardware == null ? null : hardware.getModel().getName();
    }
}
