package eu.codetopic.anty.ev3projectsandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;

import eu.codetopic.anty.ev3projectsandroid.fragment.AdvancedRemoteControl;
import eu.codetopic.anty.ev3projectsandroid.fragment.ConnectionFragment;
import eu.codetopic.anty.ev3projectsandroid.fragment.RemoteControlFragment;
import eu.codetopic.anty.ev3projectsandroid.utils.Constants;
import eu.codetopic.anty.ev3projectsbase.ClientConnection;
import eu.codetopic.utils.ui.activity.navigation.NavigationActivity;

public class MainActivity extends NavigationActivity {

    private final BroadcastReceiver mConnectionStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            invalidateNavigationMenu();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        if (navView != null) {
            navView.setItemTextColor(ContextCompat.getColorStateList(this, android.R.color.white));
            navView.setItemIconTintList(ContextCompat.getColorStateList(this, android.R.color.white));
        }*/

        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectionStateChangedReceiver,
                new IntentFilter(Constants.ACTION_BRICK_CONNECTED_STATE_CHANGED));
        mConnectionStateChangedReceiver.onReceive(this, null);
    }

    @Override
    protected Class<? extends Fragment> getMainFragmentClass() {
        return ConnectionFragment.class;
    }

    @Override
    protected boolean onUpdateSelectedNavigationMenuItem(@Nullable Fragment currentFragment, Menu menu) {
        if (currentFragment == null) return super.onUpdateSelectedNavigationMenuItem(null, menu);

        Class<? extends Fragment> fragmentClass = currentFragment.getClass();

        if (fragmentClass.equals(ConnectionFragment.class)) {
            menu.findItem(R.id.nav_connection).setChecked(true);
            return true;
        } else if (fragmentClass.equals(RemoteControlFragment.class)) {
            menu.findItem(R.id.nav_remote_control).setChecked(true);
            return true;
        } else if (fragmentClass.equals(AdvancedRemoteControl.class)) {
            menu.findItem(R.id.nav_advanced_remote_control).setChecked(true);
            return true;
        }

        return super.onUpdateSelectedNavigationMenuItem(currentFragment, menu);
    }

    @Override
    protected boolean onCreateNavigationMenu(Menu menu) {
        super.onCreateNavigationMenu(menu);
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        if (BuildConfig.DEBUG) menu.findItem(R.id.nav_debug).setVisible(true);

        if (!ClientConnection.isModelSet()) {
            replaceFragment(ConnectionFragment.class);
            return true;
        }

        menu.findItem(R.id.nav_remote_control).setVisible(true);// TODO: 27.9.16 make visible here all items that requires to be connected
        menu.findItem(R.id.nav_advanced_remote_control).setVisible(true);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_connection) {
            replaceFragment(ConnectionFragment.class);
            return true;
        } else if (id == R.id.nav_remote_control) {
            replaceFragment(RemoteControlFragment.class);
            return true;
        } else if (id == R.id.nav_advanced_remote_control) {
            replaceFragment(AdvancedRemoteControl.class);
            return true;
        }

        return super.onNavigationItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mConnectionStateChangedReceiver);
        super.onDestroy();
    }

    @Override
    protected void onBeforeReplaceFragment(FragmentTransaction ft, Fragment fragment) {
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
    }
}
