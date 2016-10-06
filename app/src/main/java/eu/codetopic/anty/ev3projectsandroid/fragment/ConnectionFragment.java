package eu.codetopic.anty.ev3projectsandroid.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.codetopic.anty.ev3projectsandroid.R;
import eu.codetopic.anty.ev3projectsandroid.utils.Constants;
import eu.codetopic.anty.ev3projectsandroid.utils.ModelInfoCustomItem;
import eu.codetopic.anty.ev3projectsbase.ClientConnection;
import eu.codetopic.anty.ev3projectsbase.ModelInfo;
import eu.codetopic.utils.Utils;
import eu.codetopic.utils.log.Log;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.job.network.NetworkJob;
import eu.codetopic.utils.ui.activity.fragment.TitleProvider;
import eu.codetopic.utils.ui.activity.navigation.NavigationFragment;
import eu.codetopic.utils.ui.container.adapter.CustomItemAdapter;

public class ConnectionFragment extends NavigationFragment implements TitleProvider {

    private static final String LOG_TAG = "ConnectionFragment";

    @BindView(R.id.buttonConnectDisconnect) public Button mButtonConnectDisconnect;
    @BindView(R.id.buttonAddModel) public Button mButtonAddModel;
    @BindView(R.id.spinnerSelectModel) public Spinner mSpinnerSelectModel;
    @BindView(R.id.editTextConnect) public EditText mEditTextConnect;
    private Unbinder mUnbinder;
    private CustomItemAdapter<ModelInfoCustomItem> mModelsAdapter;
    private final BroadcastReceiver mConnectionStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean connected = ClientConnection.isConnected();
            mButtonConnectDisconnect.setText(connected ? R.string.but_disconnect : R.string.but_connect);
            if (connected) mEditTextConnect.setText(ClientConnection.getConnectionAddress());
            mEditTextConnect.setEnabled(!connected);
            if (connected) {
                String activeModelName = ClientConnection.getActiveModelName();
                List<ModelInfoCustomItem> items = mModelsAdapter.getItems();
                for (int i = 0, size = items.size(); i < size; i++) {
                    ModelInfo modelInfo = items.get(i).getModelInfo();
                    if (Objects.equals(activeModelName, modelInfo == null ? null : modelInfo.name)) {
                        mSpinnerSelectModel.setSelection(i);
                        break;
                    }
                }
            }
            mSpinnerSelectModel.setEnabled(!connected);
            mButtonAddModel.setEnabled(!connected);
        }
    };

    @Nullable
    @Override
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);

        mModelsAdapter = new CustomItemAdapter<>(getContext(), ModelInfoCustomItem
                .wrapAll(Constants.getAvailableModels(getContext())));
        mSpinnerSelectModel.setAdapter(mModelsAdapter.forSpinner());
        mButtonConnectDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkJob.start(getHolder(), new ConnectDisconnectWork(getContext(),
                        mEditTextConnect.getText().toString(), (ModelInfo) mSpinnerSelectModel.getSelectedItem()));
            }
        });
        mButtonAddModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mConnectionStateChangedReceiver,
                new IntentFilter(Constants.ACTION_BRICK_CONNECTED_STATE_CHANGED));
        mConnectionStateChangedReceiver.onReceive(getContext(), null);
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mConnectionStateChangedReceiver);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        mModelsAdapter = null;
        mUnbinder.unbind();
        mUnbinder = null;
        super.onDestroyView();
    }

    @Override
    public CharSequence getTitle() {
        return getText(R.string.title_fragment_connect);
    }

    private static final class ConnectDisconnectWork implements NetworkJob.Work {

        private static final String LOG_TAG = ConnectionFragment.LOG_TAG + "$ConnectDisconnectWork";

        private final Context context;
        private final String ipAddress;
        private final ModelInfo model;

        ConnectDisconnectWork(Context context, String ipAddress, ModelInfo model) {
            this.context = context.getApplicationContext();
            this.ipAddress = ipAddress;
            this.model = model;
        }

        @Override
        public void run() throws Throwable {
            try {
                if (ClientConnection.isConnected()) {
                    ClientConnection.disconnect();
                } else {
                    ClientConnection.connect(ipAddress);// TODO: 5.10.16 separate setup model and connect
                    ClientConnection.setupModel(model);
                }
            } catch (Throwable t) {
                Log.d(LOG_TAG, "run", t);
                JobUtils.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, Utils.getFormattedText(context,
                                R.string.toast_text_action_failed, ipAddress), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
