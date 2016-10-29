package eu.codetopic.anty.ev3projectsbase.slam.base.map;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Point;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Rectangle;

public class FoldingOccupancyMap<T extends ByteMap & RectangleMap> extends AbsOccupancyMap {

    private static final String LOG_TAG = "FoldingOccupancyMap";

    private final int foldWidth;
    private final int foldHeight;
    private final Rectangle boundingRect;
    private final OccupancyMapLoader<T> mapLoader;

    private int actualIdX = 0;
    private int actualIdY = 0;
    private int actualXStart = 0;
    private int actualYStart = 0;
    private Rectangle actualMapRect = null;
    private T actualMap = null;

    public FoldingOccupancyMap(int foldWidth, int foldHeight, OccupancyMapLoader<T> mapLoader) {
        this.foldWidth = foldWidth;
        this.foldHeight = foldHeight;
        this.mapLoader = mapLoader;
        this.boundingRect = new Rectangle(0f, 0f, 0f, 0f);
    }

    private boolean isMapCreatedFor(int x, int y) {
        if (x < 0) x -= foldWidth;
        if (y < 0) y -= foldHeight;
        int targetIdX = x / foldWidth;
        int targetIdY = y / foldHeight;
        return isMapCreated(targetIdX, targetIdY);
    }

    private boolean isMapCreated(int idX, int idY) {
        return mapLoader.isMapCreated(idX, idY);
    }

    private void prepareMapFor(int x, int y) {
        if (actualMapRect != null && actualMapRect.contains(x, y)) return;

        if (x < 0) x -= foldWidth - 1;
        if (y < 0) y -= foldHeight - 1;
        int targetIdX = x / foldWidth;
        int targetIdY = y / foldHeight;
        loadMap(targetIdX, targetIdY);
    }

    private void loadMap(int idX, int idY) {
        if (actualMap != null) {
            mapLoader.saveMap(actualIdX, actualIdY, actualMap);
            actualMap = null;
        }

        actualIdX = idX;
        actualIdY = idY;

        int x = idX * foldWidth;
        int y = idY * foldHeight;
        actualXStart = x;
        actualYStart = y;
        actualMapRect = new Rectangle(x, y, foldWidth, foldHeight);

        int xe = x + foldWidth;
        int ye = y + foldHeight;
        double bx = boundingRect.getX();
        double by = boundingRect.getY();
        x = Math.min(x, (int) bx);
        y = Math.min(y, (int) by);
        xe = Math.max(xe, (int) (bx + boundingRect.getWidth()));
        ye = Math.max(ye, (int) (by + boundingRect.getHeight()));
        boundingRect.setFrame(x, y, xe - x, ye - y);
        notifyBoundingRectChanged(boundingRect);

        actualMap = mapLoader.createOrLoadMap(idX, idY, foldWidth, foldHeight);
    }

    @Override
    protected void setInternal(int x, int y, byte to) {
        prepareMapFor(x, y);
        actualMap.set(x - actualXStart, y - actualYStart, to);
    }

    @Override
    public byte get(int x, int y) {
        prepareMapFor(x, y);
        return actualMap.get(x - actualXStart, y - actualYStart);
    }

    @Override
    public float getDistanceToWallFrom(Pose pose) {
        pose = pose.clone();
        prepareMapFor((int) pose.getX(), (int) pose.getY());
        float range = actualMap.getDistanceToWallFrom(pose);
        while (Float.isInfinite(range)) {
            float angle = pose.getHeading();
            float pX = pose.getX();
            float pY = pose.getY();

            double aX = Math.cos(Math.toRadians(angle));
            double aY = Math.sin(Math.toRadians(angle));

            double distanceX = -1d;
            if (aX != 0d) {
                double x = actualMapRect.getX();
                x += aX > 0d ? actualMapRect.getWidth() : -1;
                distanceX = (x - pX) / aX;
            }

            double distanceY = -1d;
            if (aY != 0d) {
                double y = actualMapRect.getY();
                y += aY > 0d ? actualMapRect.getHeight() : -1;
                distanceY = (y - pY) / aY;
            }

            double targetDistance;
            if (Double.isNaN(distanceX) || distanceX < 0d) {
                if (Double.isNaN(distanceY) || distanceY < 0d) break;
                targetDistance = distanceY;
            } else {
                if (Double.isNaN(distanceY) || distanceY < 0d) targetDistance = distanceX;
                else targetDistance = Math.min(distanceX, distanceY);
            }

            float targetX = pX + (float) (aX * targetDistance);
            float targetY = pY + (float) (aY * targetDistance);

            if (!isMapCreatedFor((int) targetX, (int) targetY)) break;
            prepareMapFor((int) targetX, (int) targetY);
            pose.setLocation(targetX - actualXStart, targetY - actualYStart);
            range = actualMap.getDistanceToWallFrom(pose);
            pose.setLocation(targetX, targetY);
        }
        return range;
    }

    @Override
    public boolean isInside(Point p) {
        return true;
    }

    @Override
    public Rectangle getBoundingRect() {
        return boundingRect;
    }

    public interface OccupancyMapLoader<T extends ByteMap & RectangleMap> {

        boolean isMapCreated(int idX, int idY);

        void saveMap(int idX, int idY, T map);

        T createOrLoadMap(int idX, int idY, int mapWidth, int mapHeight);
    }
}
