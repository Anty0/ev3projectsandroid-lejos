package eu.codetopic.anty.ev3projectslego.menu;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import eu.codetopic.anty.ev3projectsbase.RMIModes;
import eu.codetopic.anty.ev3projectslego.menu.base.BeaconFollow;
import eu.codetopic.anty.ev3projectslego.menu.base.GraphicsScanner;
import eu.codetopic.anty.ev3projectslego.menu.base.TestForward;
import eu.codetopic.anty.ev3projectslego.menu.base.TestRotate;
import eu.codetopic.anty.ev3projectslego.utils.looper.Looper;

public class RMIModesImpl implements RMIModes {

    private static final String LOG_TAG = "RMIModesImpl";
    private static final DualHashBidiMap<BasicMode, Class<? extends BaseMode>> AVAILABLE_MODES;
    private static RMIModesImpl INSTANCE;

    static {
        AVAILABLE_MODES = new DualHashBidiMap<>();
        AVAILABLE_MODES.put(BasicMode.TEST_FORWARD, TestForward.class);
        AVAILABLE_MODES.put(BasicMode.TEST_ROTATE, TestRotate.class);
        AVAILABLE_MODES.put(BasicMode.BEACON_FOLLOW, BeaconFollow.class);
        AVAILABLE_MODES.put(BasicMode.GRAPHICS_SCAN_LINES, GraphicsScanner.GraphicsScannerLines.class);
        AVAILABLE_MODES.put(BasicMode.GRAPHICS_SCAN_DOTS, GraphicsScanner.GraphicsScannerDots.class);
    }

    private final Looper looper;
    private BasicMode runningMode = null;

    private RMIModesImpl(Looper looper) {
        this.looper = looper;
    }

    public static synchronized void initialize(Looper looper) {
        if (INSTANCE != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        INSTANCE = new RMIModesImpl(looper);
    }

    public static synchronized RMIModesImpl getInstance() {
        return INSTANCE;
    }

    void notifyModeStarted(@NotNull Class<? extends BaseMode> clazz) {
        BasicMode mode = AVAILABLE_MODES.getKey(clazz);
        if (runningMode != null) throw new IllegalStateException("Cannot start mode " + mode
                + ". Mode " + runningMode + " is running now.");
        runningMode = mode;
    }

    void notifyModeQuited(@NotNull Class<? extends BaseMode> clazz) {
        BasicMode mode = AVAILABLE_MODES.getKey(clazz);
        if (!Objects.equals(runningMode, mode))
            throw new IllegalStateException("Cannot quit mode " + mode
                    + ". Mode is not in running state (what???). Mode " + runningMode + " is running now.");
        runningMode = null;
    }

    @Override
    public boolean isSupported(@NotNull BasicMode mode) {
        try {
            return (Boolean) AVAILABLE_MODES.get(mode).getMethod("isSupported").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isRunning(@Nullable BasicMode mode) {
        return mode == null ? runningMode != null : Objects.equals(runningMode, mode);
    }

    @Override
    public boolean start(@NotNull BasicMode mode) {
        if (runningMode != null) return false;// TODO: 6.10.16 detect running mode started from menu
        try {
            AVAILABLE_MODES.get(mode).newInstance().start(looper);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
