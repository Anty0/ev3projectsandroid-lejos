package eu.codetopic.anty.ev3projectslego.utils.looper;

import org.jetbrains.annotations.NotNull;

import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;

public abstract class DrawableLoopJob extends LoopJob {

    private static final String LOG_TAG = "DrawableLoopJob";

    private final Canvas canvas;
    private final boolean autoClose;
    private volatile boolean invalidated = true;

    public DrawableLoopJob(Canvas canvas) {
        this(canvas, false);
    }

    public DrawableLoopJob(Canvas canvas, boolean autoClose) {
        this.canvas = canvas;
        this.autoClose = autoClose;
    }

    @Override
    protected void onStart(@NotNull Looper looper) {
        super.onStart(looper);
        invalidate();
    }

    @Override
    protected boolean handleLoop() {
        boolean toReturn = onUpdate();
        if (invalidated) {
            invalidated = false;
            drawInternal();
        }
        return toReturn;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void invalidate() {
        invalidated = true;
    }

    public void draw() {
        if (!isActive()) throw new IllegalStateException("Can't draw: start job first");
        if (Thread.currentThread() != getMyLooper().getThread())
            throw new RuntimeException("Can't call onDraw from non my looper thread.");
        drawInternal();
    }

    private void drawInternal() {
        Canvas canvas = getCanvas();
        canvas.getDrawer().clear();
        onDraw(canvas);
        canvas.apply();
    }

    protected boolean onUpdate() {
        return false;
    }

    protected abstract void onDraw(Canvas canvas);

    @Override
    protected void onQuit(@NotNull Looper looper) {
        super.onQuit(looper);
        if (autoClose) canvas.removeSelf();
    }
}
