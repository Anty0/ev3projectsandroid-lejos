package eu.codetopic.anty.ev3projectsbase.slam.base.path.heuristic;

import eu.codetopic.anty.ev3projectsbase.slam.base.map.TileBasedMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.AStarHeuristic;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.AStarTargetDetector;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.Mover;

/**
 * A heuristic that uses the tile that is closest to the target
 * as the next best tile.
 *
 * @author Kevin Glass
 */
public class ClosestHeuristic implements AStarHeuristic {
    /**
     * @see AStarHeuristic#getCost(TileBasedMap, Mover, int, int, int, int, AStarTargetDetector)
     */
    public float getCost(TileBasedMap map, Mover mover, int sx, int sy, int x, int y, AStarTargetDetector targetDetector) {
        if (!targetDetector.isTargetKnown(mover, map, sx, sy))
            throw new IllegalArgumentException("ClosestHeuristic cannot be used with targetDetector with unknown target");

        float dx = targetDetector.getKnownTargetX(mover, map, sx, sy) - x;
        float dy = targetDetector.getKnownTargetY(mover, map, sx, sy) - y;

        return (float) (Math.sqrt((dx * dx) + (dy * dy)));
    }

}