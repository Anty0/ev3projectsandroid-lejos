package eu.codetopic.anty.ev3projectsandroid.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.codetopic.anty.ev3projectsandroid.R;
import eu.codetopic.anty.ev3projectsandroid.utils.ModeStarterWork;
import eu.codetopic.utils.thread.job.network.NetworkJob;
import eu.codetopic.utils.ui.activity.fragment.TitleProvider;

import static eu.codetopic.anty.ev3projectsbase.RMIModes.BasicMode.LINE_FOLLOW;

public class LineFollowFragment extends BaseControlFragment implements TitleProvider {

    private static final String LOG_TAG = "LineFollowFragment";

    @BindView(R.id.buttonStart) public Button mButtonStart;
    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_line_follow, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);// TODO: 28.9.16 update enabled of buttons based on is supported state of modes
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkJob.start(new ModeStarterWork(getContext(), LINE_FOLLOW));// TODO: 4.1.17 advanced mode starting
            }
        });
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        mUnbinder = null;
        super.onDestroyView();
    }


    @Override
    public CharSequence getTitle() {
        return getText(R.string.title_fragment_line_follow);
    }

}
