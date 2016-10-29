package eu.codetopic.anty.ev3projectsandroid.fragment.slam.base.path;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.anty.ev3projectsandroid.fragment.slam.base.PoseHolder;
import eu.codetopic.anty.ev3projectsbase.slam.base.SlamMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Point;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.RMISlamMode;
import eu.codetopic.anty.ev3projectsbase.slam.base.mcl.MCLPoseProvider;
import eu.codetopic.anty.ev3projectsbase.slam.base.move.RotateMove;
import eu.codetopic.anty.ev3projectsbase.slam.base.move.TravelMove;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.AStarPathFinder;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.AStarTargetDetector;
import eu.codetopic.anty.ev3projectsbase.slam.base.path.Path;

public class PathManager {

    private static final String LOG_TAG = "PathManager";

    private final List<OnPathUpdatedListener> listeners = new ArrayList<>();
    private final AStarTargetDetector targetDetector;
    private final AStarPathFinder pathFinder;

    public PathManager(SlamMap map) {
        int vehicleSize = 10;// TODO: add to and load from model info
        this.targetDetector = new MoveToUnknownPlaceTargetDetector(map, vehicleSize);
        this.pathFinder = new AStarPathFinder(map, -1, vehicleSize, true,
                new DistanceToUnknownHeuristic(map, vehicleSize));
    }

    public void addOnPoseUpdatedListener(OnPathUpdatedListener listener) {
        listeners.add(listener);
        listener.onPathUpdated(null);
    }

    public boolean removeOnPoseUpdatedListener(OnPathUpdatedListener listener) {
        return listeners.remove(listener);
    }

    @Nullable
    private Path generatePath(Pose pose) {
        return pathFinder.findPath(null, (int) pose.getX(), (int) pose.getY(), targetDetector);
    }

    public void makeMove(RMISlamMode slamMode, PoseHolder poseHolder, MCLPoseProvider mcl, int tilesLimit) {
        Path path = generatePath(poseHolder.getPose());
        if (path == null || path.isEmpty()) return;

        Path.Step first = path.nextStep();
        Point target = new Point(first.getX(), first.getY());

        for (int i = 0; i < tilesLimit; i++) {
            Path.Step step = path.nextStep();
            if (step == null || (first.getX() != step.getX() && first.getY() != step.getY())) {
                moveTo(slamMode, poseHolder, mcl, target);
                if (step == null) {
                    target = null;
                    break;
                } else {
                    first = step;
                    target = new Point(first.getX(), first.getY());
                }
            }
        }

        if (target != null) moveTo(slamMode, poseHolder, mcl, target);
    }

    private void moveTo(RMISlamMode slamMode, PoseHolder poseHolder, MCLPoseProvider mcl, Point point) {
        poseHolder.updatePose(slamMode.getOdometryPose());
        mcl.applyMove(poseHolder.getPose());
        Pose moveStartPose = poseHolder.getPose();
        double aX = point.getX() - moveStartPose.getX(), aY = point.getY() - moveStartPose.getY();
        double angle = Math.toDegrees(Math.atan2(aY, aX));
        double distance = Math.sqrt(Math.pow(aX, 2) + Math.pow(aY, 2));

        slamMode.move(new RotateMove((float) (angle - moveStartPose.getHeading())));
        slamMode.move(new TravelMove((float) distance));
    }

    public interface OnPathUpdatedListener {
        void onPathUpdated(@Nullable List<Path.Step> path);
    }
}
