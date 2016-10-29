package eu.codetopic.anty.ev3projectslego.hardware;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;

import eu.codetopic.anty.ev3projectsbase.ModelInfo;
import eu.codetopic.anty.ev3projectslego.hardware.model.Model;
import lejos.hardware.ev3.LocalEV3;
import lejos.internal.ev3.EV3LED;

public final class Hardware implements Closeable {
// TODO: 6.10.16 maybe add change listeners

    private static final String LOG_TAG = "Hardware";
    public static EV3LED LED = (EV3LED) LocalEV3.get().getLED();
    private static Hardware INSTANCE = null;
    private final Model model;

    private Hardware(@NotNull Model model) {
        this.model = model;
        this.model.initialize(LocalEV3.get());
    }

    public static synchronized void saveMyModelInfo(@Nullable ModelInfo modelInfo) {// TODO: 9.10.16 add to RMIHardware
        // TODO: 9.10.16 implement
    }

    @Nullable
    @Contract(" -> _")// TODO: 9.10.16 remove contract after impl
    public static synchronized ModelInfo loadMyModelInfo() {
        return null;// TODO: 9.10.16 implement
    }

    public static synchronized Hardware get() {
        return INSTANCE;
    }

    @Contract("null -> null")
    public static synchronized Hardware setup(@Nullable Model model) {
        if (INSTANCE != null) {
            try {
                INSTANCE.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            INSTANCE = null;
        }
        if (model == null) return null;
        INSTANCE = new Hardware(model);
        return INSTANCE;
    }

    public static synchronized boolean isSet() {
        return INSTANCE != null;
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void close() throws IOException {
        model.close();
    }
}
