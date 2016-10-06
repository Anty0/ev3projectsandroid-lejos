package eu.codetopic.anty.ev3projectsbase;

import org.jetbrains.annotations.Nullable;

public interface RMIHardware {

    void setup(@Nullable ModelInfo model);

    boolean isSet();

    String getActiveModelName();
}
