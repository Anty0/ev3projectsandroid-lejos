package eu.codetopic.anty.ev3projectsandroid.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.anty.ev3projectsandroid.R;
import eu.codetopic.utils.ui.activity.fragment.TitleProvider;

public class RemoteControlFragment extends BaseControlFragment implements TitleProvider {// TODO: 28.9.16 complete

    private static final String LOG_TAG = "RemoteControlFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_remote_control, container, false);
    }

    @Override
    public CharSequence getTitle() {
        return getText(R.string.title_fragment_remote_control);
    }
}
