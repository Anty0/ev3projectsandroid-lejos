package eu.codetopic.anty.ev3projectsbase.slam.base.path;

import eu.codetopic.anty.ev3projectsbase.slam.base.map.TileBasedMap;

public interface AStarTargetDetector {

    boolean isTarget(Mover mover, TileBasedMap map, int sx, int sy, int x, int y);

    boolean isTargetKnown(Mover mover, TileBasedMap map, int sx, int sy);

    Integer getKnownTargetX(Mover mover, TileBasedMap map, int sx, int sy);

    Integer getKnownTargetY(Mover mover, TileBasedMap map, int sx, int sy);
}
