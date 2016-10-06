package eu.codetopic.anty.ev3projectslego.menu;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import eu.codetopic.anty.ev3projectslego.utils.looper.LoopJob;
import eu.codetopic.anty.ev3projectslego.utils.looper.Looper;

public abstract class BaseMode extends LoopJob {

    private static final String LOG_TAG = "BaseMode";

    private Canvas canvas = null;
    private boolean removeCanvas = false;

    @Contract(" -> _")
    public static boolean isSupported() {
        return true;
    }

    public void setCanvas(Canvas canvas, boolean removeCanvasOnQuit) {
        this.canvas = canvas;
        this.removeCanvas = removeCanvasOnQuit;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    protected void onStart(@NotNull Looper looper) {
        RMIModesImpl.getInstance().notifyModeStarted(getClass());
        super.onStart(looper);
    }

    @Override
    protected final boolean handleLoop() {
        try {
            run();
            return true;
        } finally {
            quit();
        }
    }

    public abstract void run();

    @Override
    protected void onQuit(@NotNull Looper looper) {
        super.onQuit(looper);
        if (removeCanvas) {
            canvas.removeSelf();
            canvas = null;
        }
        RMIModesImpl.getInstance().notifyModeQuited(getClass());
    }
}
