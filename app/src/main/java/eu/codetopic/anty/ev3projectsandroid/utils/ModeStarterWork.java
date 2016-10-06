package eu.codetopic.anty.ev3projectsandroid.utils;

import eu.codetopic.anty.ev3projectsbase.ClientConnection;
import eu.codetopic.anty.ev3projectsbase.RMIModes;
import eu.codetopic.anty.ev3projectsbase.RMIModes.BasicMode;
import eu.codetopic.utils.thread.job.network.NetworkJob;

public class ModeStarterWork implements NetworkJob.Work {

    private static final String LOG_TAG = "ModeStarterWork";

    private final BasicMode mode;

    public ModeStarterWork(BasicMode mode) {

        this.mode = mode;
    }

    @Override
    public void run() throws Throwable {
        RMIModes modes = ClientConnection.getModes();
        if (modes != null) modes.start(mode);
    }
}
