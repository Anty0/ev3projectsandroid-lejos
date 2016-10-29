package eu.codetopic.anty.ev3projectsandroid.fragment.slam.base;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.anty.ev3projectsandroid.fragment.slam.base.path.PathManager;
import eu.codetopic.anty.ev3projectsbase.ClientConnection;
import eu.codetopic.anty.ev3projectsbase.RMIModes;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.RMISlamMode;
import eu.codetopic.anty.ev3projectsbase.slam.base.mcl.MCLParticleSet;
import eu.codetopic.anty.ev3projectsbase.slam.base.mcl.MCLPoseProvider;
import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;
import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.thread.job.network.NetworkJob;

public class SlamClient {

    static final int MAP_PARTITION_SIZE = 1500;
    private static final String LOG_TAG = "SlamClient";
    private static final int NUM_PARTICLES = 250;
    private static final float START_X = MAP_PARTITION_SIZE / 2f;
    private static final float START_Y = MAP_PARTITION_SIZE / 2f;

    private final List<OnParticlesUpdatedListener> listeners = new ArrayList<>();
    private final PoseHolder poseHolder = new PoseHolder(new Pose(START_X, START_Y, 0f));
    private final MapManager mapManager = new MapManager();
    private final PathManager pathManager = new PathManager(mapManager.getMap());
    private MCLPoseProvider mcl;
    private RMISlamMode slamClientMode;
    private boolean run = false;

    public SlamClient() {
        NetworkJob.start(new NetworkJob.Work() {
            @Override
            public void run() throws Throwable {
                Pose pose = poseHolder.getPose();
                mcl = new MCLPoseProvider(mapManager.getMap(), NUM_PARTICLES, pose);
                RMIModes modes = ClientConnection.getModes();
                if (modes == null) return;
                slamClientMode = (RMISlamMode) modes
                        .getModeController(RMIModes.BasicMode.SLAM_CLIENT);
                slamClientMode.start();
                slamClientMode.setOdometryPose(pose);
            }
        });
    }

    public void addOnParticlesUpdatedListener(OnParticlesUpdatedListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
            listener.onParticlesUpdated(mcl == null ? null : mcl.getParticles());
        }
    }

    public boolean removeOnParticlesUpdatedListener(OnParticlesUpdatedListener listener) {
        synchronized (listeners) {
            return listeners.remove(listener);
        }
    }

    private void onParticlesUpdated() {
        MCLParticleSet particleSet = mcl.getParticles();
        synchronized (listeners) {
            for (OnParticlesUpdatedListener listener : listeners) {
                listener.onParticlesUpdated(particleSet);
            }
        }
    }

    public PoseHolder getPoseHolder() {
        return poseHolder;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public PathManager getPathManager() {
        return pathManager;
    }

    public boolean isRunning() {
        return run;
    }

    public void start() {
        run = true;
        NetworkJob.start(new NetworkJob.Work() {
            @Override
            public void run() throws Throwable {
                Log.d(LOG_TAG, "run: starting");
                try {
                    while (run) {
                        float maxDistance;
                        {
                            poseHolder.updatePose(slamClientMode.getOdometryPose());
                            Log.d(LOG_TAG, "run: scanning: pose=" + poseHolder.getPose());
                            ScanResults results = slamClientMode.scan();
                            Log.d(LOG_TAG, "run: update pose: started");
                            mcl.applyMove(poseHolder.getPose());
                            onParticlesUpdated();
                            Log.d(LOG_TAG, "run: update pose: move apply completed");
                            if (mcl.update(results)) {
                                Log.d(LOG_TAG, "run: update pose: successful, pose=" + poseHolder.getPose());
                                poseHolder.updatePose(mcl.getPose());
                                slamClientMode.setOdometryPose(poseHolder.getPose());
                            } else {
                                Log.d(LOG_TAG, "run: update pose: unsuccessful, pose=" + poseHolder.getPose());
                            }

                            Log.d(LOG_TAG, "run: writing results to map: started");
                            mapManager.applyScanOnMap(poseHolder.getPose(), results);
                            Log.d(LOG_TAG, "run: writing results to map: completed");

                            maxDistance = results.getTotalMaxDistance();
                        }

                        if (!run) break;

                        pathManager.makeMove(slamClientMode, poseHolder, mcl, (int) (maxDistance / 5f));
                        onParticlesUpdated();
                    }
                } catch (Throwable t) {
                    Log.e(LOG_TAG, "run", t);
                    run = false;
                }
                Log.d(LOG_TAG, "run: exiting");
            }
        });
    }

    public void stop() {
        run = false;
    }

    public void close() {
        stop();
        NetworkJob.start(new NetworkJob.Work() {
            @Override
            public void run() throws Throwable {
                slamClientMode.stop();
                slamClientMode = null;
                mcl = null;
            }
        });
    }

    public interface OnParticlesUpdatedListener {
        void onParticlesUpdated(@Nullable MCLParticleSet particleSet);
    }
}
