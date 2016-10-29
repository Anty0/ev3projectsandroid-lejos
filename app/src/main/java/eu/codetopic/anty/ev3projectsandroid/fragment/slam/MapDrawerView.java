package eu.codetopic.anty.ev3projectsandroid.fragment.slam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.anty.ev3projectsandroid.fragment.slam.base.PoseHolder;
import eu.codetopic.anty.ev3projectsandroid.fragment.slam.base.SlamClient;
import eu.codetopic.anty.ev3projectsandroid.fragment.slam.base.path.PathManager;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Point;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Rectangle;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.OccupancyMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.mcl.MCLParticle;
import eu.codetopic.anty.ev3projectsbase.slam.base.mcl.MCLParticleSet;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.Path;
import eu.codetopic.java.utils.log.Log;

public class MapDrawerView extends View implements SlamClient.OnParticlesUpdatedListener,
        PathManager.OnPathUpdatedListener, PoseHolder.OnPoseUpdatedListener, OccupancyMap.OnMapChangeListener {

    private static final String LOG_TAG = "MapDrawerView";

    private final Paint mPaint = new Paint();
    private final OccupancyMapCache mMapCache = new OccupancyMapCache();
    private MCLParticleSet mParticles = null;
    private List<Path.Step> mPath = null;
    private Pose mActualPose = new Pose(0f, 0f, 0f);

    public MapDrawerView(Context context) {
        super(context);
    }

    public MapDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapDrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MapDrawerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void register(SlamClient client) {
        client.getPoseHolder().addOnPoseUpdatedListener(this);
        client.getMapManager().addOnChangeListener(this);
        client.getPathManager().addOnPoseUpdatedListener(this);
        client.addOnParticlesUpdatedListener(this);
    }

    public void unregister(SlamClient client) {
        client.getPoseHolder().removeOnPoseUpdatedListener(this);
        client.getMapManager().removeOnChangeListener(this);
        client.getPathManager().removeOnPoseUpdatedListener(this);
        client.removeOnParticlesUpdatedListener(this);
    }

    @Override
    public void onParticlesUpdated(@Nullable MCLParticleSet particleSet) {
        synchronized (mPaint) {
            mParticles = particleSet;
        }
        postInvalidate();
    }

    @Override
    public void onPathUpdated(@Nullable List<Path.Step> path) {
        synchronized (mPaint) {
            mPath = path == null ? null : new ArrayList<>(path);
        }
        postInvalidate();
    }

    @Override
    public void onPoseUpdated(Pose newPose) {
        synchronized (mPaint) {
            mActualPose = newPose.clone();
        }
        postInvalidate();
    }

    @Override
    public void onMapChange(final OccupancyMap map, final int x, final int y, final byte newVal) {
        synchronized (mPaint) {
            mMapCache.onMapChange(map, x, y, newVal);
        }
        postInvalidate();

    }

    @Override
    public void onMapChange(OccupancyMap map, Point[] changedPoints) {
        synchronized (mPaint) {
            mMapCache.onWholeMapChange(map);
            //mMapCache.onMapChange(map, changedPoints);// TODO: 27.10.16 why it don't work
        }
        postInvalidate();
    }

    @Override
    public void onWholeMapChange(OccupancyMap map) {
        synchronized (mPaint) {
            mMapCache.onWholeMapChange(map);
        }
        postInvalidate();

    }

    @Override
    public void onMapBoundingRectChange(final OccupancyMap map, final Rectangle newBoundingRect) {
        synchronized (mPaint) {
            mMapCache.onMapBoundingRectChange(map, newBoundingRect);
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {// TODO: 27.10.16 more draw methods
        canvas.drawColor(0xFF888888);

        synchronized (mPaint) {
            canvas.save();
            float centerXAdd = getWidth() / 2f;
            float centerYAdd = getHeight() / 2f;
            canvas.translate(centerXAdd - mActualPose.getX(), centerYAdd - mActualPose.getY());
            //canvas.rotate(mActualPose.getHeading(), centerXAdd, centerYAdd);

            mMapCache.drawMapCache(canvas);

            if (mPath != null) {
                mPaint.setColor(Color.BLUE);
                Point last = mActualPose.getLocation().clone();
                for (Path.Step step : mPath) {
                    canvas.drawLine((float) last.getX(), (float) last.getY(), step.getX(), step.getY(), mPaint);
                    last.setLocation(step.getX(), step.getY());
                }
            }

            {
                mPaint.setColor(Color.RED);
                float x = mActualPose.getX();
                float y = mActualPose.getY();
                canvas.drawCircle(x, y, 8f, mPaint);

                float angle = mActualPose.getHeading();
                double rAngle = Math.toRadians(angle);
                float eX = (float) (Math.cos(rAngle) * 14f) + x;
                float eY = (float) (Math.sin(rAngle) * 14f) + y;
                canvas.drawLine(x, y, eX, eY, mPaint);

                {
                    double rLessAngle = Math.toRadians(angle - 90);
                    float sLessX = (float) (Math.cos(rLessAngle) * 8f) + x;
                    float sLessY = (float) (Math.sin(rLessAngle) * 8f) + y;
                    canvas.drawLine(sLessX, sLessY, eX, eY, mPaint);
                }

                {
                    double rMoreAngle = Math.toRadians(angle + 90);
                    float sMoreX = (float) (Math.cos(rMoreAngle) * 8f) + x;
                    float sMoreY = (float) (Math.sin(rMoreAngle) * 8f) + y;
                    canvas.drawLine(sMoreX, sMoreY, eX, eY, mPaint);
                }
            }

            if (mParticles != null) {
                try {
                    mPaint.setColor(Color.GREEN);
                    for (MCLParticle particle : mParticles.getParticles()) {
                        Pose pose = particle.getPose();
                        canvas.drawCircle(pose.getX(), pose.getY(), 2f, mPaint);
                    }

                    mPaint.setColor(Color.YELLOW);
                    float max = mParticles.getMaxWeight();
                    for (MCLParticle particle : mParticles.getParticles()) {
                        Pose pose = particle.getPose();
                        canvas.drawCircle(pose.getX(), pose.getY(), particle.getWeight() / max * 2f, mPaint);
                    }
                } catch (Exception e) {
                    Log.d(LOG_TAG, "onDraw", e);
                }
            }

            canvas.restore();
        }
        super.onDraw(canvas);
    }
}
