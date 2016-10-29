package eu.codetopic.anty.ev3projectsandroid.fragment.slam.base;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.anty.ev3projectsandroid.fragment.slam.RamOccupancyMapLoader;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Point;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Rectangle;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.FoldingOccupancyMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.OccupancyMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.OccupancyMapImpl;
import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResult;
import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;
import eu.codetopic.java.utils.log.Log;

import static eu.codetopic.anty.ev3projectsandroid.fragment.slam.base.SlamClient.MAP_PARTITION_SIZE;

public class MapManager {

    private static final String LOG_TAG = "MapManager";

    private static final int[][][] MAP_ADDING_PATTERNS =
            {{
                    {-1, -1}, {-1, 0}, {-1, 1},
                    {0, -1}, {0, 0}, {0, 1},
                    {1, -1}, {1, 0}, {1, 1}
            }, {
                    {-1, 1},
                    {-1, 0}, {0, 1},
                    {-1, -1}, {0, 0}, {1, 1},
                    {0, -1}, {1, 0},
                    {1, -1}
            }, {
                    {-1, 1}, {0, 1}, {1, 1},
                    {-1, 0}, {0, 0}, {1, 0},
                    {-1, -1}, {0, -1}, {1, -1}
            }, {
                    {1, 1},
                    {0, 1}, {1, 0},
                    {-1, 1}, {0, 0}, {1, -1},
                    {-1, 0}, {0, -1},
                    {-1, -1}
            }, {
                    {1, 1}, {1, 0}, {1, -1},
                    {0, 1}, {0, 0}, {0, -1},
                    {-1, 1}, {-1, 0}, {-1, -1}
            }, {
                    {1, -1},
                    {1, 0}, {0, -1},
                    {1, 1}, {0, 0}, {-1, -1},
                    {0, 1}, {-1, 0},
                    {-1, 1}
            }, {
                    {1, -1}, {0, -1}, {-1, -1},
                    {1, 0}, {0, 0}, {-1, 0},
                    {1, 1}, {0, 1}, {-1, 1}
            }, {
                    {-1, -1},
                    {0, -1}, {-1, 0},
                    {1, -1}, {0, 0}, {-1, 1},
                    {1, 0}, {0, 1},
                    {1, 1}
            }};

    private final FoldingOccupancyMap<OccupancyMapImpl> map;
    private final List<OccupancyMap.OnMapChangeListener> listeners = new ArrayList<>();

    public MapManager() {
        map = new FoldingOccupancyMap<>(MAP_PARTITION_SIZE, MAP_PARTITION_SIZE,
                new RamOccupancyMapLoader());// TODO: 17.10.16 save maps to shared preferences
        map.addOnChangeListener(new OccupancyMap.OnMapChangeListener() {
            @Override
            public void onMapBoundingRectChange(OccupancyMap map, Rectangle newBoundingRect) {
                synchronized (listeners) {
                    for (OccupancyMap.OnMapChangeListener listener : listeners) {
                        listener.onMapBoundingRectChange(map, newBoundingRect);
                    }
                }
            }

            @Override
            public void onMapChange(OccupancyMap map, int x, int y, byte newVal) {

            }

            @Override
            public void onMapChange(OccupancyMap map, Point[] changedPoints) {

            }

            @Override
            public void onWholeMapChange(OccupancyMap map) {

            }
        });
    }

    public void addOnChangeListener(OccupancyMap.OnMapChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public boolean removeOnChangeListener(OccupancyMap.OnMapChangeListener listener) {
        synchronized (listeners) {
            return listeners.remove(listener);
        }
    }

    public FoldingOccupancyMap getMap() {
        return map;
    }

    public void applyScanOnMap(Pose scanPose, ScanResults results) {// TODO: 28.10.16 connect wall points if there is not scanned place
        //List<Point> changedPoints = SetUniqueList.setUniqueList(new ArrayList<Point>());
        Point point = null;
        for (ScanResult result : results) {
            float scanX = scanPose.getX();
            float scanY = scanPose.getY();
            float angle = scanPose.getHeading() + result.getAngle();
            boolean usable = result.isUsable();
            float maxDistance = result.getMaxDistance();
            float distance = usable ? result.getDistance() : maxDistance;
            double rAngle = Math.toRadians(angle);
            //float x = (float) Math.cos(rAngle);
            //float y = (float) Math.sin(rAngle);

            double cosAngleX = Math.cos(rAngle);
            double sinAngleY = Math.sin(rAngle);

            int modId;
            if (cosAngleX > 0.4f) {
                if (sinAngleY > 0.4f) modId = 3;
                else if (sinAngleY < -0.4f) modId = 5;
                else modId = 4;
            } else if (cosAngleX < -0.4f) {
                if (sinAngleY < -0.4f) modId = 7;
                else if (sinAngleY > 0.4f) modId = 1;
                else modId = 0;
            } else {
                if (sinAngleY > 0.4f) modId = 2;
                else if (sinAngleY < -0.4f) modId = 6;
                else modId = -1;
            }

            if (modId == -1) {
                Log.e(LOG_TAG, "Problem detected, can't detect direction of scan result " + result);
                continue;
            }

            //x *= distance;
            //y *= distance;

            for (int k = 0, maxK = (int) (usable ? distance - 1 : maxDistance); k < maxK; k++) {
                float tX = (float) (cosAngleX * k), tY = (float) (sinAngleY * k);
                int x = (int) (scanX + tX), y = (int) (scanY + tY);
                float weight = (maxDistance - k) / maxDistance;
                map.reduce(x, y, (byte) (weight * OccupancyMap.CELL_RANGE));
                //changedPoints.add(new Point(x, y));
            }

            if (usable) {
                {
                    int x = (int) (scanX + (cosAngleX * distance)),
                            y = (int) (scanY + (sinAngleY * distance));
                    float weight = (maxDistance - distance) / maxDistance;
                    byte increaseVal = (byte) (weight * OccupancyMap.CELL_RANGE);

                    if (point == null) {
                        map.increase(x, y, increaseVal);
                        //changedPoints.add(new Point(x, y));
                    } else {
                        int x0 = x, y0 = y, x1 = (int) point.getX(), y1 = (int) point.getY();
                        // Uses Bresenham's line algorithm
                        int dy = y1 - y0;
                        int dx = x1 - x0;
                        int stepx, stepy;

                        if (dy < 0) {
                            dy = -dy;
                            stepy = -1;
                        } else {
                            stepy = 1;
                        }
                        if (dx < 0) {
                            dx = -dx;
                            stepx = -1;
                        } else {
                            stepx = 1;
                        }
                        dy <<= 1; // dy is now 2*dy
                        dx <<= 1; // dx is now 2*dx

                        map.increase(x0, y0, increaseVal);
                        //changedPoints.add(new Point(x, y));
                        if (dx > dy) {
                            int fraction = dy - (dx >> 1);  // same as 2*dy - dx
                            while (x0 != x1) {
                                if (fraction >= 0) {
                                    y0 += stepy;
                                    fraction -= dx; // same as fraction -= 2*dx
                                }
                                x0 += stepx;
                                fraction += dy; // same as fraction -= 2*dy
                                map.increase(x0, y0, increaseVal);
                                //changedPoints.add(new Point(x, y));
                            }
                        } else {
                            int fraction = dx - (dy >> 1);
                            while (y0 != y1) {
                                if (fraction >= 0) {
                                    x0 += stepx;
                                    fraction -= dy;
                                }
                                y0 += stepy;
                                fraction += dx;
                                map.increase(x0, y0, increaseVal);
                            }
                        }
                    }

                    point = new Point(x, y);
                }

                for (int k = (int) distance, maxK = (int) maxDistance; k < maxK; k++) {
                    float tX = (float) (cosAngleX * k), tY = (float) (sinAngleY * k);
                    int x = (int) (scanX + tX), y = (int) (scanY + tY);
                    if (map.get(x, y) == OccupancyMap.CELL_UNKNOWN) {
                        map.set(x, y, OccupancyMap.CELL_NOT_SCANNABLE);
                        //changedPoints.add(new Point(x, y));
                    }
                }
            } else {
                point = null;
            }

            /*for (int[] mod : MAP_ADDING_PATTERNS[modId]) {
                float mX = x + mod[0], mY = y + mod[1];
                float weightModify = mod[0] == 0 && mod[1] == 0 ? 1f : 2f;

                double radiansAngle = Math.atan2(mY, mX);
                double cosAngle = Math.cos(radiansAngle);
                double sinAngle = Math.sin(radiansAngle);
                for (int k = 0, maxK = (int) (usable ? distance - 2 : maxDistance); k < maxK; k++) {
                    float tX = (float) (cosAngle * k), tY = (float) (sinAngle * k);
                    map.reduce((int) (scanPose.getX() + tX), (int) (scanPose.getY() + tY),
                            (byte) (((maxDistance - k) / maxDistance / weightModify) * OccupancyMap.CELL_RANGE));
                }

                if (usable) {
                    map.increase((int) (scanPose.getX() + mX), (int) (scanPose.getY() + mY),
                            (byte) ((weight / weightModify) * OccupancyMap.CELL_RANGE));
                }
            }*/

                        /*for (int i = -1; i <= 1; i++) {// TODO: remove if new way will work well
                            for (int j = -1; j <= 1; j++) {
                                float mX = x + i, mY = y + j;

                                for (int k = 0; k < distance; k++) {
                                    float angle = Math.toDegrees(Math.atan2(mY/mX));
                                    float tX = (float) Math.cos(Math.toRadians(angle)) * k, tY = (float) Math.sin(Math.toRadians(angle)) * k;
                                    //if (!map.isInside(new Point(tX, tY))) continue;// always false
                                    map.reduce((int) (pose.getX() + tX), (int) (pose.getY() + tY),
                                            (byte) (((maxDistance - k) / maxDistance) * OccupancyMap.CELL_RANGE));
                                }

                                //if (!map.isInside(new Point(tX, tY))) continue;// always false
                                map.increase((int) (pose.getX() + mX), (int) (pose.getY() + mY),
                                        (byte) (weight * OccupancyMap.CELL_RANGE));
                            }
                        }*/
        }

        //if (!changedPoints.isEmpty()) {}
        synchronized (listeners) {
            /*if (!listeners.isEmpty()) {
                Point[] changedPointsArray = changedPoints.toArray(new Point[changedPoints.size()]);
            }*/
            for (OccupancyMap.OnMapChangeListener listener : listeners) {
                listener.onWholeMapChange(map);
            }
        }

    }
}
