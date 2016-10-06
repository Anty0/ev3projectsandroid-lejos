package eu.codetopic.anty.ev3projectslego.hardware;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;

import eu.codetopic.anty.ev3projectslego.Main;
import eu.codetopic.anty.ev3projectslego.hardware.model.Model;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.ev3.LocalEV3;

public final class Hardware implements Closeable {

    private static final String LOG_TAG = "Hardware";
    private static Hardware INSTANCE = null;

    private final EV3 ev3;
    private final Model model;

    private Hardware(@NotNull Model model) {
        this.ev3 = LocalEV3.get();
        BrickFinder.setDefault(ev3);
        this.model = model;
        this.model.initialize(ev3);
    }

    public static synchronized Hardware get() {
        return INSTANCE;
    }

    public static synchronized Hardware setup(@Nullable Model model) {// TODO: 6.10.16 add option to upload default model info to brick program and apply it as default on initialize
        try {
            if (INSTANCE != null) {
                try {
                    INSTANCE.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                INSTANCE = null;
            }
            if (model == null) return get();
            INSTANCE = new Hardware(model);
            return get();

        } finally {
            Main.MAIN_MENU.reloadItems();// TODO: 6.10.16 maybe find better way
        }
    }

    public static synchronized boolean isSet() {
        return INSTANCE != null;
    }

    public EV3 getEv3() {
        return ev3;
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void close() throws IOException {
        model.close();
    }
}
