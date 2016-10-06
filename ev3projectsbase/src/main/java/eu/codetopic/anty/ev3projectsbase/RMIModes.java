package eu.codetopic.anty.ev3projectsbase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RMIModes {

    boolean isSupported(@NotNull BasicMode mode);

    boolean isRunning(@Nullable BasicMode mode);

    boolean start(@NotNull BasicMode mode);

    enum BasicMode {
        TEST_FORWARD, TEST_ROTATE, BEACON_FOLLOW, GRAPHICS_SCAN_LINES, GRAPHICS_SCAN_DOTS
    }
}
