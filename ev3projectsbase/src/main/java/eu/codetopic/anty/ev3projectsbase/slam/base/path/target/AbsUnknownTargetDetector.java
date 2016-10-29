package eu.codetopic.anty.ev3projectsbase.slam.base.path.target;

import org.jetbrains.annotations.Nullable;

import eu.codetopic.anty.ev3projectsbase.slam.base.map.TileBasedMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.AStarTargetDetector;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.Mover;

/**
 * Created by anty on 26.10.16.
 *
 * @author anty
 */
public abstract class AbsUnknownTargetDetector implements AStarTargetDetector {

    private static final String LOG_TAG = "AbsUnknownTargetDetector";

    @Override
    public boolean isTargetKnown(Mover mover, TileBasedMap map, int sx, int sy) {
        return false;
    }

    @Nullable
    @Override
    public Integer getKnownTargetX(Mover mover, TileBasedMap map, int sx, int sy) {
        return null;
    }

    @Nullable
    @Override
    public Integer getKnownTargetY(Mover mover, TileBasedMap map, int sx, int sy) {
        return null;
    }
}
