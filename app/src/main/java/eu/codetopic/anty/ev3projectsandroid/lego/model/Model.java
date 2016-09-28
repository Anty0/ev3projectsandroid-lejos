package eu.codetopic.anty.ev3projectsandroid.lego.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;

import eu.codetopic.anty.ev3projectsandroid.utils.DistanceRangeFinder;
import eu.codetopic.utils.ui.container.items.custom.MultilineCustomItem;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.robotics.RangeScanner;
import lejos.robotics.navigation.ArcRotateMoveController;

public abstract class Model extends MultilineCustomItem implements Closeable {

    public static final double DEG_TO_RAD_MUL = Math.PI / 180d;

    public static Model[] getAvailableModels(Context context) {
        return new Model[]{new DefaultModel()};// TODO: 28.9.16 load user-created models
    }

    @NonNull
    public abstract String getName();

    public abstract void initialize(RemoteRequestEV3 ev3) throws IOException;

    @Nullable
    public abstract ArcRotateMoveController getPilot();

    @Nullable
    public abstract DistanceRangeFinder getDistanceRangeFinder();

    @Nullable
    public abstract RangeScanner getRangeScanner();

    @Nullable
    @Override
    public CharSequence getTitle(Context context, int position) {
        return getName();
    }

    @Nullable
    @Override
    public CharSequence getText(Context context, int position) {
        return null;
    }
}
