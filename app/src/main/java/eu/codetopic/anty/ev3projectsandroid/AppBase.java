package eu.codetopic.anty.ev3projectsandroid;

import android.app.Application;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import eu.codetopic.anty.ev3projectsandroid.utils.Constants;
import eu.codetopic.anty.ev3projectsbase.ClientConnection;
import eu.codetopic.utils.Utils;
import eu.codetopic.utils.UtilsBase;
import eu.codetopic.utils.UtilsBase.ProcessProfile;
import eu.codetopic.utils.thread.job.SingletonJobManager;

import static eu.codetopic.utils.UtilsBase.InitType.INIT_NORMAL_MODE;

public class AppBase extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Application app = this;
        UtilsBase.initialize(this, new ProcessProfile(getPackageName(), INIT_NORMAL_MODE, new Runnable() {
            @Override
            public void run() {
                //AppData.initialize(app);
                SingletonJobManager.initialize(app);
                //SingletonDatabase.initialize(new AppDatabase(app));

                //TimedComponentsManager.initialize(app, SettingsData.getter.get()
                //        .getRequiredNetworkType(), FavoriteItemsRefreshService.class);

                ClientConnection.addOnChangeListener(new ClientConnection.ConnectionChangeListener() {
                    @Override
                    public void onConnected(String address) {
                        onChange(address);
                    }

                    @Override
                    public void onDisconnected(String address) {
                        onChange(address);
                    }

                    @Override
                    public void onForceDisconnected(String address) {
                        onChange(address);
                        Toast.makeText(app, Utils.getFormattedText(app, R.string
                                .toast_text_connection_lost, address), Toast.LENGTH_LONG).show();
                    }

                    private void onChange(String address) {
                        LocalBroadcastManager.getInstance(app).sendBroadcast(
                                new Intent(Constants.ACTION_BRICK_CONNECTED_STATE_CHANGED)
                                        .putExtra(Constants.EXTRA_EV3_ADDRESS, address));
                    }
                });
            }
        }));
    }
}
