package eu.codetopic.anty.ev3projectslego.utils.looper;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessagingJob extends LoopJob {

    private static final String LOG_TAG = "MessagingJob";

    private final Queue<Message> mMessageQueue = new LinkedBlockingQueue<>();

    public MessagingJob() {
    }

    public void sendMessage(Message message) {
        mMessageQueue.offer(message);
    }

    @Override
    protected boolean handleLoop() {
        boolean toReturn = false;
        Message message;
        while ((message = mMessageQueue.poll()) != null) {
            // TODO: 6.10.16 implement
            toReturn = true;
        }
        return toReturn;
    }
}
