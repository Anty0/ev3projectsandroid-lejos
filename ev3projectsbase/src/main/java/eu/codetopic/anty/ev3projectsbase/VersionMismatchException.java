package eu.codetopic.anty.ev3projectsbase;

import java.io.IOException;

public class VersionMismatchException extends IOException {

    private static final String LOG_TAG = "VersionMismatchException";

    public VersionMismatchException() {
    }

    public VersionMismatchException(String s) {
        super(s);
    }

    public VersionMismatchException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public VersionMismatchException(Throwable throwable) {
        super(throwable);
    }
}
