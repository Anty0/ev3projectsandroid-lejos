package eu.codetopic.anty.ev3projectsandroid.utils;

import android.content.Context;
import android.widget.Toast;

import java.io.Serializable;

import eu.codetopic.anty.ev3projectsbase.ClientConnection;
import eu.codetopic.anty.ev3projectsbase.RMIModes;
import eu.codetopic.anty.ev3projectsbase.RMIModes.BasicMode;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.job.network.NetworkJob;

public class ModeStarterWork implements NetworkJob.Work, Serializable {

    private static final String LOG_TAG = "ModeStarterWork";

    private final Context context;
    private final BasicMode mode;

    public ModeStarterWork(Context context, BasicMode mode) {
        this.context = context.getApplicationContext();
        this.mode = mode;
    }

    @Override
    public void run() throws Throwable {
        RMIModes modes = ClientConnection.getModes();
        if (modes == null || !modes.start(mode)) {
            JobUtils.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Failed to start mode: " + mode.getModeName(), Toast.LENGTH_LONG).show();
                }
            });
            // TODO: 7.10.16 show warning to user about not started mode
        }
    }
}
