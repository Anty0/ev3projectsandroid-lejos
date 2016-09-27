package eu.codetopic.anty.ev3projectsandroid.lego;

import android.content.Intent;

import java.io.Closeable;
import java.io.IOException;

import eu.codetopic.anty.ev3projectsandroid.AppBase;
import lejos.hardware.BrickFinder;
import lejos.remote.ev3.RemoteRequestEV3;

public final class Hardware implements Closeable {// TODO: 27.9.16 make it more universal and add option to configure own model

    private static final String LOG_TAG = "Hardware";
    public static final String ACTION_CONNECTED_STATE_CHANGED =
            "eu.codetopic.anty.ev3projectsandroid.lego." + LOG_TAG + ".CONNECTED_STATE_CHANGED";
    private static Hardware INSTANCE = null;

    private final String brickAddress;
    private final RemoteRequestEV3 ev3;

    private Hardware(String brickAddress) throws IOException {
        this.brickAddress = brickAddress;
        this.ev3 = new RemoteRequestEV3(brickAddress);
        BrickFinder.setDefault(ev3);
        // TODO: 27.9.16 initialize other hardware
    }

    public static synchronized Hardware get() {
        return INSTANCE;
    }

    public static synchronized Hardware connect(String brickAddress) throws IOException {
        disconnectInternal();
        INSTANCE = new Hardware(brickAddress);
        AppBase.broadcasts.sendBroadcast(new Intent(ACTION_CONNECTED_STATE_CHANGED));
        return get();
    }

    public static synchronized void disconnect() throws IOException {
        disconnectInternal();
        AppBase.broadcasts.sendBroadcast(new Intent(ACTION_CONNECTED_STATE_CHANGED));
    }

    private static synchronized void disconnectInternal() throws IOException {
        if (INSTANCE == null) return;
        INSTANCE.close();
        INSTANCE = null;
    }

    public static synchronized boolean isConnected() {
        return INSTANCE != null;
    }


    public String getBrickAddress() {
        return brickAddress;
    }

    public RemoteRequestEV3 getEv3() {
        return ev3;
    }

    @Override
    public void close() throws IOException {
        ev3.disConnect();
    }
}
