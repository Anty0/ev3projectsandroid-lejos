package eu.codetopic.anty.ev3projectsandroid.fragment.slam;

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
import eu.codetopic.anty.ev3projectsandroid.fragment.BaseControlFragment;
import eu.codetopic.anty.ev3projectsandroid.fragment.slam.base.SlamClient;
import eu.codetopic.utils.ui.activity.fragment.TitleProvider;

public class SlamControlFragment extends BaseControlFragment implements TitleProvider {

    private static final String LOG_TAG = "SlamControlFragment";
    private final SlamClient mSlamClient = new SlamClient();
    @BindView(R.id.mapDrawer) public MapDrawerView mMapDrawer;
    @BindView(R.id.buttonStart) public Button mButtonStart;
    private Unbinder mUnbinder;

    public SlamControlFragment() {
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slam_control, container, false);// TODO: 27.10.16 add view indicating state of SlamClient
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSlamClient.isRunning()) mSlamClient.stop();
                else mSlamClient.start();
                updateButtonText();
            }
        });
        updateButtonText();

        mMapDrawer.register(mSlamClient);
    }

    private void updateButtonText() {
        mButtonStart.setText(mSlamClient.isRunning() ? R.string.but_stop : R.string.but_start);
    }

    @Override
    public void onDestroyView() {
        mMapDrawer.unregister(mSlamClient);
        mSlamClient.close();

        mUnbinder.unbind();
        mUnbinder = null;
        super.onDestroyView();
    }

    @Override
    public CharSequence getTitle() {
        return getText(R.string.title_fragment_slam_control);
    }
}
