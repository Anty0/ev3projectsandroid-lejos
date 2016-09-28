package eu.codetopic.anty.ev3projectsandroid.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import eu.codetopic.anty.ev3projectsandroid.R;
import eu.codetopic.anty.ev3projectsandroid.lego.Hardware;
import eu.codetopic.utils.ui.activity.navigation.NavigationFragment;

public abstract class BaseControlFragment extends NavigationFragment {

    private static final String LOG_TAG = "BaseControlFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Hardware.isConnected()) {
            removeSelfFragment();
            Toast.makeText(getContext(), R.string.toast_text_connection_required, Toast.LENGTH_LONG).show();
        }
    }

}
