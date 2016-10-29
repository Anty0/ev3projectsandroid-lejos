package eu.codetopic.anty.ev3projectsbase.slam.base.map;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Rectangle;

public class OccupancyMapImpl extends AbsOccupancyMap {

    private final Rectangle boundingRect;
    private final byte[][] cells;

    public OccupancyMapImpl(int width, int height) {
        this.boundingRect = new Rectangle(0f, 0f, width, height);
        this.cells = new byte[width][height];
    }

    @Override
    protected void setInternal(int x, int y, byte to) {
        cells[x][y] = to;
    }

    @Override
    public byte get(int x, int y) {
        return cells[x][y];
    }

    @Override
    public Rectangle getBoundingRect() {
        return boundingRect;
    }
}
