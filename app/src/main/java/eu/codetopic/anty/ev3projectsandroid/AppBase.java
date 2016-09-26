package eu.codetopic.anty.ev3projectsandroid;

import android.app.Application;

import eu.codetopic.utils.UtilsBase;
import eu.codetopic.utils.UtilsBase.ProcessProfile;

import static eu.codetopic.utils.UtilsBase.InitType.INIT_NORMAL_MODE;

public class AppBase extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //final Application app = this;
        UtilsBase.initialize(this, new ProcessProfile(getPackageName(), INIT_NORMAL_MODE/*, new Runnable() {
            @Override
            public void run() {
                //AppData.initialize(app);
                //SingletonJobManager.initialize(app);
                //SingletonDatabase.initialize(new AppDatabase(app));

                //TimedComponentsManager.initialize(app, SettingsData.getter.get()
                //        .getRequiredNetworkType(), FavoriteItemsRefreshService.class);
            }
        }*/));
    }
}
