package eu.codetopic.anty.ev3projectsbase.slam.base.path.target;

import org.jetbrains.annotations.Nullable;

import eu.codetopic.anty.ev3projectsbase.slam.base.map.TileBasedMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.AStarTargetDetector;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.Mover;

public class KnownTargetDetector implements AStarTargetDetector {

    private static final String LOG_TAG = "KnownTargetDetector";

    private final int x, y;

    public KnownTargetDetector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean isTarget(Mover mover, TileBasedMap map, int sx, int sy, int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean isTargetKnown(Mover mover, TileBasedMap map, int sx, int sy) {
        return true;
    }

    @Nullable
    @Override
    public Integer getKnownTargetX(Mover mover, TileBasedMap map, int sx, int sy) {
        return x;
    }

    @Nullable
    @Override
    public Integer getKnownTargetY(Mover mover, TileBasedMap map, int sx, int sy) {
        return y;
    }
}
