package eu.codetopic.anty.ev3projectsandroid.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.codetopic.anty.ev3projectsandroid.R;
import eu.codetopic.anty.ev3projectsbase.ModelInfo;
import eu.codetopic.utils.ui.container.items.custom.MultilineCustomItem;

public class ModelInfoCustomItem extends MultilineCustomItem {

    private static final String LOG_TAG = "ModelInfoCustomItem";

    private final ModelInfo modelInfo;

    public ModelInfoCustomItem(@Nullable ModelInfo modelInfo) {
        this.modelInfo = modelInfo;
    }

    public static List<ModelInfoCustomItem> wrapAll(Collection<ModelInfo> models) {
        List<ModelInfoCustomItem> result = new ArrayList<>();
        for (ModelInfo model : models) result.add(new ModelInfoCustomItem(model));
        return result;
    }

    public static List<ModelInfoCustomItem> wrapAll(ModelInfo[] models) {
        List<ModelInfoCustomItem> result = new ArrayList<>();
        for (ModelInfo model : models) result.add(new ModelInfoCustomItem(model));
        return result;
    }

    @Nullable
    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    @Nullable
    @Override
    public CharSequence getTitle(Context context, int position) {
        return modelInfo == null ? context.getText(R.string.text_no_model) : modelInfo.name;
    }

    @Nullable
    @Override
    public CharSequence getText(Context context, int position) {
        return null;
    }
}
