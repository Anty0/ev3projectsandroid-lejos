package eu.codetopic.anty.ev3projectsandroid.fragment.slam;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Point;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Rectangle;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.OccupancyMap;
import eu.codetopic.java.utils.log.Log;

public class OccupancyMapCache implements OccupancyMap.OnMapChangeListener {

    private static final String LOG_TAG = "OccupancyMapCache";

    private final Paint mMapPaint = new Paint();
    private final Paint mBitmapPaint = new Paint();
    private final Object mLock = new Object();
    private boolean mFirst = true;
    private Rectangle mActualMapRect = new Rectangle(0f, 0f, 1f, 1f);
    private Bitmap mMapCache = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    private Canvas mMapCacheDrawer = new Canvas(mMapCache);

    public void clear() {
        synchronized (mLock) {
            mActualMapRect = new Rectangle(0f, 0f, 1f, 1f);
            mMapCache.recycle();
            mMapCache = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            mMapCacheDrawer = new Canvas(mMapCache);
            mFirst = true;
        }
    }

    private void prepareFor(Rectangle rectangle) {
        synchronized (mLock) {
            if (mActualMapRect.getX() == rectangle.getX()
                    && mActualMapRect.getY() == rectangle.getY()
                    && mActualMapRect.getHeight() == rectangle.getHeight()
                    && mActualMapRect.getWidth() == rectangle.getWidth()) return;

            Log.d(LOG_TAG, "prepareFor " + rectangle);

            if (!mFirst && !rectangle.contains(mActualMapRect)) {
                Log.d(LOG_TAG, "New map rectangle not contains old map rectangle," +
                        " old cache can be particularly lost," +
                        " cache must be prepared again for properly work");
            }

            Rectangle oMapRect = mActualMapRect;
            Bitmap oMapCache = mMapCache;

            Rectangle nMapRect = (Rectangle) rectangle.clone();
            Bitmap nMapCache = Bitmap.createBitmap(Math.max((int) nMapRect.getWidth(), 1),
                    Math.max((int) nMapRect.getHeight(), 1), Bitmap.Config.ARGB_8888);
            Canvas nMapCacheDrawer = new Canvas(nMapCache);
            nMapCacheDrawer.drawBitmap(oMapCache, (float) (oMapRect.getX() - nMapRect.getX()),
                    (float) (oMapRect.getY() - nMapRect.getY()), mBitmapPaint);

            mActualMapRect = nMapRect;
            mMapCache.recycle();
            mMapCache = nMapCache;
            mMapCacheDrawer = nMapCacheDrawer;
            mFirst = false;
        }
    }

    @Override
    public void onMapBoundingRectChange(OccupancyMap map, Rectangle newBoundingRect) {
        prepareFor(newBoundingRect);
    }

    @Override
    public void onMapChange(OccupancyMap map, int x, int y, byte newVal) {
        drawMapPoint(map, x, y, newVal);
    }

    @Override
    public void onMapChange(OccupancyMap map, Point[] changedPoints) {
        synchronized (mLock) {
            for (Point point : changedPoints) {
                int x = (int) point.getX(), y = (int) point.getY();
                drawMapPoint(map, x, y, map.get(x, y));
            }
        }
    }

    @Override
    public void onWholeMapChange(OccupancyMap map) {
        synchronized (mLock) {
            clear();
            prepareFor(map.getBoundingRect());

            for (int x = (int) mActualMapRect.getX(), maxX = x + (int) mActualMapRect.getWidth(); x < maxX; x++) {
                for (int y = (int) mActualMapRect.getY(), maxY = y + (int) mActualMapRect.getHeight(); y < maxY; y++) {
                    drawMapPoint(map, x, y, map.get(x, y));
                }
            }
        }
    }

    private void drawMapPoint(OccupancyMap map, int x, int y, byte val) {
        synchronized (mLock) {
            @ColorInt int color = val == OccupancyMap.CELL_NOT_SCANNABLE
                    ? Color.BLACK : generateColorFor(map, val);
            mMapPaint.setColor(color);
            mMapCacheDrawer.drawPoint(x - (int) mActualMapRect.getX(),
                    y + (int) mActualMapRect.getY(), mMapPaint);
        }
    }

    private int generateColorFor(OccupancyMap map, byte val) {
        float occupancy = map.toPercent(val);

        int transColorInt = (int) (Math.abs(occupancy - 0.5f) * 2 * 0xFF);
        int colorInt = Math.abs((int) (occupancy * 0xFF) - 0xFF);
        return Color.argb(transColorInt, colorInt, colorInt, colorInt);
    }

    public void drawMapCache(Canvas canvas) {
        synchronized (mLock) {
            canvas.drawBitmap(mMapCache, (int) mActualMapRect.getX(),
                    (int) mActualMapRect.getY(), null);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mMapCache.recycle();
    }
}
