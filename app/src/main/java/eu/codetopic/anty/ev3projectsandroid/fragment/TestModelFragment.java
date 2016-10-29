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

import static eu.codetopic.anty.ev3projectsbase.RMIModes.BasicMode.TEST_FORWARD;
import static eu.codetopic.anty.ev3projectsbase.RMIModes.BasicMode.TEST_ROTATE;

public class TestModelFragment extends BaseControlFragment implements TitleProvider {// TODO: 28.9.16 complete

    private static final String LOG_TAG = "TestModelFragment";

    @BindView(R.id.buttonTestForward) public Button mButtonTestForward;
    @BindView(R.id.buttonTestRotate) public Button mButtonTestRotate;
    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_model, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);// TODO: 28.9.16 update enabled of buttons based on is supported state of modes
        mButtonTestForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkJob.start(new ModeStarterWork(getContext(), TEST_FORWARD));
            }
        });
        mButtonTestRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkJob.start(new ModeStarterWork(getContext(), TEST_ROTATE));
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
        return getText(R.string.title_fragment_test_model);
    }
}
