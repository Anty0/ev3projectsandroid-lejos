package eu.codetopic.anty.ev3projectsbase.slam.base.map;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.anty.ev3projectsbase.slam.base.SlamMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Point;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Range;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Rectangle;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.Mover;

public abstract class AbsOccupancyMap implements SlamMap {// TODO: 28.10.16 make AbsSlamMap extends (AbsOccupancyMap implements only OccupancyMap)

    private static final String LOG_TAG = "AbsOccupancyMap";

    private final List<OnMapChangeListener> listeners = new ArrayList<>();

    @Override
    public void addOnChangeListener(OnMapChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
            listener.onWholeMapChange(this);
        }
    }

    @Override
    public boolean removeOnChangeListener(OnMapChangeListener listener) {
        synchronized (listeners) {
            return listeners.remove(listener);
        }
    }

    protected void notifyBoundingRectChanged(Rectangle boundingRect) {
        synchronized (listeners) {
            for (OnMapChangeListener listener : listeners) {
                listener.onMapBoundingRectChange(this, boundingRect);
            }
        }
    }

    @Override
    public void increase(int x, int y, byte by) {
        byte occupied = get(x, y);
        if (occupied == CELL_UNKNOWN || occupied == CELL_NOT_SCANNABLE)
            occupied = CELL_CENTERED_UNKNOWN;
        set(x, y, (byte) (occupied + ((CELL_OCCUPIED_END - (occupied - CELL_FREE_START)) / (float) CELL_RANGE) * by));
    }

    @Override
    public void reduce(int x, int y, byte by) {
        byte occupied = get(x, y);
        if (occupied == CELL_UNKNOWN || occupied == CELL_NOT_SCANNABLE)
            occupied = CELL_CENTERED_UNKNOWN;
        set(x, y, (byte) (occupied - ((occupied - CELL_FREE_START) / (float) CELL_RANGE) * by));
    }

    @Override
    public void set(int x, int y, byte to) {
        setInternal(x, y, to);
        synchronized (listeners) {
            for (OnMapChangeListener listener : listeners) {
                listener.onMapChange(this, x, y, to);
            }
        }
    }

    protected abstract void setInternal(int x, int y, byte to);

    @Override
    public abstract byte get(int x, int y);

    @Override
    public float getOccupiedPercent(int x, int y) {
        return toPercent(get(x, y));
    }

    @Override
    public float toPercent(byte occupied) {
        if (occupied == CELL_UNKNOWN || occupied == CELL_NOT_SCANNABLE)
            occupied = CELL_CENTERED_UNKNOWN;
        occupied -= CELL_FREE_START;
        return occupied / (float) CELL_RANGE;
    }

    @Override
    public boolean isUnknown(int x, int y) {
        byte occupied = get(x, y);
        return occupied == CELL_UNKNOWN || (occupied != CELL_NOT_SCANNABLE
                && occupied > CELL_FREE_START + CELL_FREE_MAX_DIFF
                && occupied < CELL_OCCUPIED_END - CELL_OCCUPIED_MAX_DIFF);
    }

    @Override
    public boolean isFree(int x, int y) {
        byte occupied = get(x, y);
        return occupied != CELL_UNKNOWN && occupied != CELL_NOT_SCANNABLE
                && occupied - CELL_FREE_START < CELL_FREE_MAX_DIFF;
    }

    @Override
    public boolean isOccupied(int x, int y) {
        byte occupied = get(x, y);
        return occupied != CELL_UNKNOWN && occupied != CELL_NOT_SCANNABLE
                && CELL_OCCUPIED_END - occupied < CELL_OCCUPIED_MAX_DIFF;
    }

    /*@Override
    public float getDistanceToWallFrom(Pose pose) {
        pose = pose.clone();
        float distance = Float.POSITIVE_INFINITY;
        for (float a = pose.getHeading(), i = a - 2.5f, max = a + 2.5f; i <= max; i+=0.25f) {
            distance = Math.min(distance, getDistanceToWallFromInternal(pose));
        }
        return distance;
    }*/

    @Override
    public float getDistanceToWallFrom(Pose pose) {// TODO: 29.10.16 optimize
        float angle = pose.getHeading();
        float pX = pose.getX();
        float pY = pose.getY();

        double aX = Math.cos(Math.toRadians(angle));
        double aY = Math.sin(Math.toRadians(angle));

        Rectangle boundingRect = getBoundingRect();

        if (!boundingRect.contains(pX, pY)) {
            Range rangeX = new Range((float) boundingRect.getX(), (float) boundingRect.getMaxX());
            Range rangeY = new Range((float) boundingRect.getY(), (float) boundingRect.getMaxY());
            do {
                if ((pX > rangeX.getEnd() && aX >= 0d)
                        || (pX < rangeX.getStart() && aX <= 0d)
                        || (pY > rangeY.getEnd() && aY >= 0d)
                        || (pY < rangeY.getStart() && aY <= 0d)) {
                    return Float.POSITIVE_INFINITY;
                }

                pX += aX / 2d;
                pY += aY / 2d;
            } while (!boundingRect.contains(pX, pY));
        }

        double x = pX, y = pY;
        float range = 0f;

        while (!isOccupied((int) x, (int) y)) {
            range += 0.5f;
            x = pX + aX * range;
            y = pY + aY * range;
            if (!boundingRect.contains(x, y)) {
                return Float.POSITIVE_INFINITY;
            }
        }

        return range;
    }

    @Override
    public boolean isInside(Point p) {
        return getBoundingRect().contains(p);
    }

    @Override
    public abstract Rectangle getBoundingRect();

    @Override
    public void pathFinderVisited(int x, int y) {
        // nothing to do
    }

    @Override
    public boolean blocked(Mover mover, int x, int y) {
        return !isFree(x, y);
    }

    @Override
    public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
        return 1f + getOccupiedPercent(tx, ty) / ((float) CELL_FREE_MAX_DIFF / (float) CELL_RANGE);
    }
}
