package eu.codetopic.anty.ev3projectsbase.slam.base.map;

import org.jetbrains.annotations.NotNull;

import eu.codetopic.anty.ev3projectsbase.RMIBasicMode;
import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.move.Move;
import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;

public interface RMISlamMode extends RMIBasicMode {

    Pose getOdometryPose();

    void setOdometryPose(@NotNull Pose pose);

    void move(@NotNull Move move);

    ScanResults scan();

    void stop();
}
