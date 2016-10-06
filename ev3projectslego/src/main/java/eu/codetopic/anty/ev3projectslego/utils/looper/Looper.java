package eu.codetopic.anty.ev3projectslego.utils.looper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Looper {

    private static final String LOG_TAG = "Looper";

    private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();

    private final List<LoopJob> jobs = new CopyOnWriteArrayList<>();
    private final Thread thread;
    private boolean running = false;
    private volatile boolean quiting = false;

    private Looper() {
        thread = Thread.currentThread();
    }

    public static void prepare() {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper());
    }

    public static
    @Nullable
    Looper myLooper() {
        return sThreadLocal.get();
    }

    public static void loop() {
        final Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        synchronized (me.thread) {
            if (me.running) {
                throw new RuntimeException("Looper is still looping on this thread.");
            }
            me.running = true;
        }

        while (!me.quiting) {
            synchronized (me.jobs) {
                for (LoopJob job : me.jobs) {
                    if (job.handleLoop()) break;
                }
            }
            Thread.yield();
        }

        synchronized (me.jobs) {
            me.jobs.forEach(LoopJob::notifyQuit);
        }

        me.quiting = false;
        synchronized (me.thread) {
            me.running = false;
        }
    }

    public boolean isCurrentThread() {
        return Thread.currentThread() == thread;
    }

    void addJob(LoopJob job) {
        synchronized (jobs) {
            jobs.add(0, job);
        }
    }

    boolean removeJob(LoopJob job) {
        synchronized (jobs) {
            return jobs.remove(job);
        }
    }

    public void quit() {
        quiting = true;
    }

    public
    @NotNull
    Thread getThread() {
        return thread;
    }

    @Override
    public String toString() {
        return "Looper (" + thread.getName() + ", tid " + thread.getId()
                + ") {" + Integer.toHexString(System.identityHashCode(this)) + "}";
    }
}
