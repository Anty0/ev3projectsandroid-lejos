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
import eu.codetopic.anty.ev3projectsandroid.lego.Hardware;
import eu.codetopic.utils.thread.job.network.NetworkJob;
import eu.codetopic.utils.ui.activity.fragment.TitleProvider;
import lejos.robotics.navigation.ArcRotateMoveController;

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
        mUnbinder = ButterKnife.bind(this, view);
        mButtonTestForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkJob.start(new ForwardWork());
            }
        });
        mButtonTestRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkJob.start(new RotateWork());
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

    private static class ForwardWork implements NetworkJob.Work {

        @Override
        public void run() throws Throwable {
            ArcRotateMoveController pilot = Hardware.get().getModel().getPilot();
            if (pilot == null) return;
            pilot.travel(50, true);
        }
    }

    private static class RotateWork implements NetworkJob.Work {

        @Override
        public void run() throws Throwable {
            ArcRotateMoveController pilot = Hardware.get().getModel().getPilot();
            if (pilot == null) return;
            pilot.rotate(10 * 360, true);
        }
    }
}
