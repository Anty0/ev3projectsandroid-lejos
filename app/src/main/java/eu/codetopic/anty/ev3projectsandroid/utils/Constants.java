package eu.codetopic.anty.ev3projectsandroid.utils;

import android.content.Context;

import eu.codetopic.anty.ev3projectsbase.DefaultModelInfo;
import eu.codetopic.anty.ev3projectsbase.ModelInfo;

public final class Constants {

    public static final String ACTION_BRICK_CONNECTED_STATE_CHANGED =
            "eu.codetopic.anty.ev3projectsandroid.CONNECTED_STATE_CHANGED";
    public static final String EXTRA_EV3_ADDRESS =
            "eu.codetopic.anty.ev3projectsandroid.EXTRA_EV3_ADDRESS";
    private static final String LOG_TAG = "Constants";

    private Constants() {
    }

    public static ModelInfo[] getAvailableModels(Context context) {
        return new ModelInfo[]{null, DefaultModelInfo.getInstance()};// TODO: 28.9.16 load user-created models
    }

}
