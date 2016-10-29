package eu.codetopic.anty.ev3projectsandroid.fragment.slam.base.path;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Rectangle;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.OccupancyMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.TileBasedMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.AStarHeuristic;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.AStarTargetDetector;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.Mover;

public class DistanceToUnknownHeuristic implements AStarHeuristic {

    private static final String LOG_TAG = "DistanceToUnknownHeuristic";

    private final OccupancyMap map;
    private final int vehicleSize;

    public DistanceToUnknownHeuristic(OccupancyMap map, int vehicleSize) {
        this.map = map;
        this.vehicleSize = vehicleSize;
    }

    public OccupancyMap getMap() {
        return map;
    }

    @Override
    public float getCost(TileBasedMap tileMap, Mover mover, int sx, int sy, int x, int y, AStarTargetDetector targetDetector) {
        int max;
        {
            Rectangle rect = map.getBoundingRect();
            max = (int) Math.max(rect.getWidth(), rect.getHeight());
        }
        int wallSearchSize = vehicleSize * 3;
        return (getDistanceToUnknown(x, y, max)) +
                (Math.abs(getDistanceToWall(x, y, wallSearchSize) - wallSearchSize) * 2f);
    }

    private float getDistanceToUnknown(int x, int y, int max) {
        int distance = 0;
        while (true) {
            for (int i = -distance; i <= distance; i++) {
                if (map.get(x - i, y - distance) == OccupancyMap.CELL_UNKNOWN
                        || map.get(x - i, y + distance) == OccupancyMap.CELL_UNKNOWN
                        || map.get(x + distance, y - i) == OccupancyMap.CELL_UNKNOWN
                        || map.get(x - distance, y - i) == OccupancyMap.CELL_UNKNOWN)
                    return distance;
            }
            distance++;
            if (distance >= max) break;
        }

        distance = 0;
        while (true) {
            for (int i = -distance; i <= distance; i++) {
                if (map.isUnknown(x - i, y - distance)
                        || map.isUnknown(x - i, y + distance)
                        || map.isUnknown(x + distance, y - i)
                        || map.isUnknown(x - distance, y - i)) return distance;
            }
            distance++;
            if (distance >= max) return distance;
        }
    }

    private float getDistanceToWall(int x, int y, int max) {
        int distance = 1;
        while (true) {
            for (int i = -distance; i <= distance; i++) {
                if (map.isOccupied(x - i, y - distance)
                        || map.isOccupied(x - i, y + distance)
                        || map.isOccupied(x + distance, y - i)
                        || map.isOccupied(x - distance, y - i)) return distance;
            }
            distance++;
            if (distance >= max) return distance;
        }
    }
}
