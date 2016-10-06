package eu.codetopic.anty.ev3projectslego.utils.looper;

import java.util.LinkedList;

import eu.codetopic.anty.ev3projectslego.utils.Utils;

public class PostJob extends LoopJob {

    private static final String LOG_TAG = "PostJob";

    private final LinkedList<Runnable> jobs = new LinkedList<>();

    public void postJob(Runnable job) {
        postJob(job, false);
    }

    public void postJob(Runnable job, boolean waitToExecute) {
        synchronized (jobs) {
            jobs.offer(job);
        }

        if (waitToExecute) {
            Utils.waitWhile(() -> {
                synchronized (jobs) {
                    return jobs.contains(job);
                }
            });
        }
    }

    @Override
    protected boolean handleLoop() {
        boolean toReturn = false;
        Runnable job;
        synchronized (jobs) {
            while ((job = jobs.poll()) != null) {
                job.run();
                toReturn = true;
            }
        }
        return toReturn;
    }
}
