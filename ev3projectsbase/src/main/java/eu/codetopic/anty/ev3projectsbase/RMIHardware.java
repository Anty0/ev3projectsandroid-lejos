package eu.codetopic.anty.ev3projectsbase;

import org.jetbrains.annotations.Nullable;

public interface RMIHardware {

    void setup(@Nullable ModelInfo model) throws Throwable;// TODO: 9.10.16 catch this throwable in android app and optionally show to user

    boolean isSet();

    String getActiveModelName();
}
