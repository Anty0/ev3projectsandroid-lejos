package eu.codetopic.anty.ev3projectsandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.ui.activity.fragment.TitleProvider;
import eu.codetopic.utils.ui.activity.navigation.NavigationFragment;

public class RemoteControlFragment extends NavigationFragment implements TitleProvider {

    private static final String LOG_TAG = "RemoteControlFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);// TODO: 27.9.16 validate ev3 connection or create parent for all fragments that uses Hardware class
    }

    @Nullable
    @Override
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateContentView(inflater, container, savedInstanceState);
    }

    @Override
    public CharSequence getTitle() {
        return getText(R.string.title_fragment_remote_control);
    }
}
