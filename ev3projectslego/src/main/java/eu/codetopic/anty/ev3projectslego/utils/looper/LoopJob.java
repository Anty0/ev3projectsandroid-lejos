package eu.codetopic.anty.ev3projectslego.utils.looper;

import org.jetbrains.annotations.NotNull;

public abstract class LoopJob {

    private static final String LOG_TAG = "LoopJob";

    private Looper looper;

    public Looper getMyLooper() {
        return looper;
    }

    public boolean isActive() {
        return looper != null;
    }

    public final void start() {
        Looper looper = Looper.myLooper();
        if (looper == null) throw new IllegalStateException(
                "No Looper; Looper.prepare() wasn't called on this thread.");
        start(looper);
    }

    public final synchronized void start(@NotNull Looper looper) {
        if (this.looper != null) throw new IllegalStateException(LOG_TAG + " is still started");
        this.looper = looper;
        this.looper.addJob(this);
        onStart(this.looper);
    }

    protected void onStart(@NotNull Looper looper) {
    }

    protected abstract boolean handleLoop();

    protected void onQuit(@NotNull Looper looper) {
    }

    public final synchronized void quit() {
        if (looper == null) throw new IllegalStateException(LOG_TAG + " is not started");
        if (looper.removeJob(this))
            onQuit(looper);
        looper = null;
    }
}
