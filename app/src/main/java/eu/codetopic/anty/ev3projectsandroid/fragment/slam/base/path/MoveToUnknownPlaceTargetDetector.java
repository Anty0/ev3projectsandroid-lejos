package eu.codetopic.anty.ev3projectsandroid.fragment.slam.base.path;

import eu.codetopic.anty.ev3projectsbase.slam.base.map.OccupancyMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.TileBasedMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.Mover;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.target.AbsUnknownTargetDetector;

public class MoveToUnknownPlaceTargetDetector extends AbsUnknownTargetDetector {

    private static final String LOG_TAG = "MoveToUnknownPlaceTargetDetector";

    private final OccupancyMap map;
    private final int vehicleSize;

    public MoveToUnknownPlaceTargetDetector(OccupancyMap map, int vehicleSize) {
        this.map = map;
        this.vehicleSize = vehicleSize;
    }

    public OccupancyMap getMap() {
        return map;
    }

    @Override
    public boolean isTarget(Mover mover, TileBasedMap tileMap, int sx, int sy, int x, int y) {
        if (Math.sqrt(Math.pow(sx - x, 2) + Math.pow(sy - y, 2)) < 10d) return false;

        int distance = 1;
        while (true) {
            for (int i = -distance; i <= distance; i++) {
                if (map.isUnknown(x - i, y - distance)
                        || map.isUnknown(x - i, y + distance)
                        || map.isUnknown(x + distance, y - i)
                        || map.isUnknown(x - distance, y - i)) return true;
            }
            distance++;
            if (distance > vehicleSize * 2f) return false;
        }
    }
}
