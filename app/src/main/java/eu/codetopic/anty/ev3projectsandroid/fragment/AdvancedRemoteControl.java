package eu.codetopic.anty.ev3projectsandroid.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.anty.ev3projectsandroid.R;
import eu.codetopic.utils.ui.activity.fragment.TitleProvider;

public class AdvancedRemoteControl extends BaseControlFragment implements TitleProvider {// TODO: 28.9.16 complete

    private static final String LOG_TAG = "AdvancedRemoteControl";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateContentView(inflater, container, savedInstanceState);
    }

    @Override
    public CharSequence getTitle() {
        return getText(R.string.title_fragment_advanced_remote_control);
    }

}
