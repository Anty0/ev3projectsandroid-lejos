package eu.codetopic.anty.ev3projectslego.mode;

import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import eu.codetopic.anty.ev3projectsbase.RMIModes;
import eu.codetopic.anty.ev3projectslego.hardware.Hardware;
import eu.codetopic.anty.ev3projectslego.mode.base.BeaconFollowMode;
import eu.codetopic.anty.ev3projectslego.mode.base.GraphicsScannerMode;
import eu.codetopic.anty.ev3projectslego.mode.base.SlamClientMode;
import eu.codetopic.anty.ev3projectslego.mode.base.TestForwardMode;
import eu.codetopic.anty.ev3projectslego.mode.base.TestRotateMode;
import eu.codetopic.anty.ev3projectslego.utils.draw.canvas.Canvas;
import eu.codetopic.anty.ev3projectslego.utils.looper.LoopJob;
import eu.codetopic.anty.ev3projectslego.utils.looper.Looper;
import lejos.internal.ev3.EV3LED;

public class RMIModesImpl implements RMIModes {

    private static final String LOG_TAG = "RMIModesImpl";
    private static final DualHashBidiMap<BasicMode, ModeController> AVAILABLE_MODES;
    private static RMIModesImpl INSTANCE;

    static {
        AVAILABLE_MODES = new DualHashBidiMap<>();
        AVAILABLE_MODES.put(BasicMode.TEST_FORWARD, new TestForwardMode());
        AVAILABLE_MODES.put(BasicMode.TEST_ROTATE, new TestRotateMode());
        AVAILABLE_MODES.put(BasicMode.BEACON_FOLLOW, new BeaconFollowMode());
        AVAILABLE_MODES.put(BasicMode.GRAPHICS_SCAN_LINES, new GraphicsScannerMode.Lines());
        AVAILABLE_MODES.put(BasicMode.GRAPHICS_SCAN_DOTS, new GraphicsScannerMode.Dots());
        AVAILABLE_MODES.put(BasicMode.SLAM_CLIENT, new SlamClientMode());
    }

    private final Looper looper;
    private final Object runningModeLock = new Object();
    private BasicMode runningMode = null;

    private RMIModesImpl(Looper looper) {
        this.looper = looper;
    }

    public static synchronized void initialize(CallHandler handler, Looper looper) throws LipeRMIException {
        if (INSTANCE != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        for (ModeController controller : AVAILABLE_MODES.values()) {
            handler.exportObject(controller.getRmiInterface(), controller);
        }
        INSTANCE = new RMIModesImpl(looper);
    }

    public static synchronized RMIModesImpl getInstance() {
        return INSTANCE;
    }

    public BasicMode getModeBasicMode(ModeController controller) {
        return AVAILABLE_MODES.getKey(controller);
    }

    @Override
    public boolean isSupported(@NotNull BasicMode mode) {
        try {
            return AVAILABLE_MODES.get(mode).isSupported();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean isRunning(@NotNull ModeController mode) {
        return isRunning(AVAILABLE_MODES.getKey(mode));
    }

    @Override
    public boolean isRunning(@Nullable BasicMode mode) {
        synchronized (runningModeLock) {
            return mode == null ? runningMode != null : Objects.equals(runningMode, mode);
        }
    }

    boolean start(@NotNull ModeController mode) {
        return start(AVAILABLE_MODES.getKey(mode));
    }

    @Override
    public boolean start(@NotNull BasicMode mode) {
        return start(mode, null/*TODO: find way to use RMICanvas*/, false);
    }

    boolean start(@NotNull ModeController mode, @Nullable Canvas canvas, boolean autoRemoveCanvas) {
        return start(AVAILABLE_MODES.getKey(mode), canvas, autoRemoveCanvas);
    }

    public boolean start(@NotNull BasicMode mode, @Nullable Canvas canvas, boolean autoRemoveCanvas) {
        synchronized (runningModeLock) {
            if (runningMode != null) return false;
            runningMode = mode;
        }
        try {
            new LoopJob() {
                @Override
                protected void onStart(@NotNull Looper looper) {
                    Hardware.LED.setPattern(EV3LED.COLOR_GREEN, EV3LED.PATTERN_HEARTBEAT);
                    super.onStart(looper);
                }

                @Override
                protected boolean handleLoop() {
                    try {
                        AVAILABLE_MODES.get(mode).onStart(canvas);
                        return true;
                    } finally {
                        quit();
                    }
                }

                @Override
                protected void onQuit(@NotNull Looper looper) {
                    super.onQuit(looper);
                    Hardware.LED.setPattern(EV3LED.COLOR_ORANGE, EV3LED.PATTERN_ON);
                    if (autoRemoveCanvas && canvas != null) canvas.removeSelf();
                    synchronized (runningModeLock) {
                        if (runningMode == mode) {
                            runningMode = null;
                        } else {
                            System.err.println("Cannot quit mode " + mode
                                    + ". Mode is not in running state (WTF???). Mode "
                                    + runningMode + " is running now.");
                        }
                    }
                }
            }.start(looper);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ModeController getModeController(@NotNull BasicMode mode) {
        return AVAILABLE_MODES.get(mode);
    }
}
