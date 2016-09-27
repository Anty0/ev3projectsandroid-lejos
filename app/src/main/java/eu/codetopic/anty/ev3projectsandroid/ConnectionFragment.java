package eu.codetopic.anty.ev3projectsandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.codetopic.anty.ev3projectsandroid.lego.Hardware;
import eu.codetopic.utils.Utils;
import eu.codetopic.utils.thread.job.SingletonJobManager;
import eu.codetopic.utils.thread.job.network.NetworkJob;
import eu.codetopic.utils.ui.activity.fragment.TitleProvider;
import eu.codetopic.utils.ui.activity.navigation.NavigationFragment;

public class ConnectionFragment extends NavigationFragment implements TitleProvider {

    private static final String LOG_TAG = "ConnectionFragment";

    @BindView(R.id.buttonConnectDisconnect) public Button mButtonConnectDisconnect;
    @BindView(R.id.editTextConnect) public EditText mEditTextConnect;
    private final BroadcastReceiver mConnectionStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean connected = Hardware.isConnected();
            mButtonConnectDisconnect.setText(connected
                    ? R.string.but_disconnect : R.string.but_connect);
            if (connected) mEditTextConnect.setText(Hardware.get().getBrickAddress());
            mEditTextConnect.setEnabled(!connected);
        }
    };
    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        mButtonConnectDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkJob.start(SingletonJobManager.getter, new NetworkJob(getHolder(),
                        Hardware.class, new ConnectDisconnectWork(getContext(),
                        mEditTextConnect.getText().toString())));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        AppBase.broadcasts.registerReceiver(mConnectionStateChangedReceiver,
                new IntentFilter(Hardware.ACTION_CONNECTED_STATE_CHANGED));
        mConnectionStateChangedReceiver.onReceive(getContext(), null);
    }

    @Override
    public void onStop() {
        AppBase.broadcasts.unregisterReceiver(mConnectionStateChangedReceiver);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        mUnbinder = null;
        super.onDestroyView();
    }

    @Override
    public CharSequence getTitle() {
        return getText(R.string.title_fragment_connect);
    }

    private static final class ConnectDisconnectWork implements NetworkJob.Work {

        private final Context context;
        private final String ipAddress;

        ConnectDisconnectWork(Context context, String ipAddress) {
            this.context = context.getApplicationContext();
            this.ipAddress = ipAddress;
        }

        @Override
        public void run() throws Throwable {
            try {
                if (Hardware.isConnected()) Hardware.disconnect();
                else Hardware.connect(ipAddress);
            } catch (Throwable t) {
                Toast.makeText(context, Utils.getFormattedText(context,
                        R.string.toast_action_failed, ipAddress), Toast.LENGTH_LONG).show();
            }
        }
    }
}
