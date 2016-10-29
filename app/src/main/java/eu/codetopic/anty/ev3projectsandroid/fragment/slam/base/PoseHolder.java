package eu.codetopic.anty.ev3projectsandroid.fragment.slam.base;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;

public class PoseHolder {

    private static final String LOG_TAG = "PoseHolder";

    private final List<OnPoseUpdatedListener> listeners = new ArrayList<>();
    private Pose pose;

    public PoseHolder(Pose pose) {
        this.pose = pose;
    }

    public void addOnPoseUpdatedListener(OnPoseUpdatedListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
            listener.onPoseUpdated(pose);
        }
    }

    public boolean removeOnPoseUpdatedListener(OnPoseUpdatedListener listener) {
        synchronized (listeners) {
            return listeners.remove(listener);
        }
    }

    public void updatePose(Pose pose) {
        this.pose = pose;
        synchronized (listeners) {
            for (OnPoseUpdatedListener listener : listeners) {
                listener.onPoseUpdated(pose);
            }
        }
    }

    public Pose getPose() {
        return pose;
    }

    public interface OnPoseUpdatedListener {
        void onPoseUpdated(Pose newPose);
    }
}
