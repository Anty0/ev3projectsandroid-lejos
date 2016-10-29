package eu.codetopic.anty.ev3projectsbase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RMIModes {

    boolean isSupported(@NotNull BasicMode mode);

    /**
     * null arg means -> is any running?
     */
    boolean isRunning(@Nullable BasicMode mode);

    boolean start(@NotNull BasicMode mode);

    RMIBasicMode getModeController(@NotNull BasicMode mode);

    enum BasicMode {
        TEST_FORWARD("Test-Forward"), TEST_ROTATE("Test-Rotate"), BEACON_FOLLOW("BeaconFollow"),
        GRAPHICS_SCAN_LINES("GraphicsScan-Lines"), GRAPHICS_SCAN_DOTS("GraphicsScan-Dots"),
        SLAM_CLIENT("SLAM Client");

        private final String name;

        BasicMode(String name) {
            this.name = name;
        }

        public String getModeName() {
            return name;
        }
    }
}
