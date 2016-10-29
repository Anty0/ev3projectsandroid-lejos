package eu.codetopic.anty.ev3projectslego.utils;

public class OutOfRangeException extends RuntimeException {

    private static final String LOG_TAG = "OutOfRangeException";

    public OutOfRangeException() {
    }

    public OutOfRangeException(String s) {
        super(s);
    }

    public OutOfRangeException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public OutOfRangeException(Throwable throwable) {
        super(throwable);
    }

    public OutOfRangeException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
