package eu.codetopic.anty.ev3projectslego.utils.scan.base;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import eu.codetopic.anty.ev3projectsbase.slam.base.scan.SeekResult;

public interface Scanner {

    float getMaxDistance();

    float fetchDistance();

    boolean hasSeek();

    @Nullable
    SeekResult fetchSeek();

    List<SeekResult> fetchAllSeek();
}
