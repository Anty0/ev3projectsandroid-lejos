package eu.codetopic.anty.ev3projectsbase.slam.base.map;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Point;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Rectangle;

public interface OccupancyMap extends ByteMap {

    byte CELL_NOT_SCANNABLE = -1;
    byte CELL_UNKNOWN = 0;
    byte CELL_FREE_START = 1;
    byte CELL_OCCUPIED_END = 127;
    byte CELL_RANGE = CELL_OCCUPIED_END - CELL_FREE_START;
    byte CELL_FREE_MAX_DIFF = CELL_RANGE / 4;
    byte CELL_OCCUPIED_MAX_DIFF = CELL_FREE_MAX_DIFF * 2 - 2;
    byte CELL_CENTERED_UNKNOWN = CELL_RANGE / 2 + CELL_FREE_START;

    void addOnChangeListener(OnMapChangeListener listener);

    boolean removeOnChangeListener(OnMapChangeListener listener);

    void increase(int x, int y, byte by);

    void reduce(int x, int y, byte by);

    void set(int x, int y, byte to);

    byte get(int x, int y);

    float getOccupiedPercent(int x, int y);

    float toPercent(byte occupied);

    boolean isUnknown(int x, int y);

    boolean isFree(int x, int y);

    boolean isOccupied(int x, int y);

    Rectangle getBoundingRect();

    interface OnMapChangeListener {
        void onMapBoundingRectChange(OccupancyMap map, Rectangle newBoundingRect);

        void onMapChange(OccupancyMap map, int x, int y, byte newVal);

        void onMapChange(OccupancyMap map, Point[] changedPoints);

        void onWholeMapChange(OccupancyMap map);
    }
}
