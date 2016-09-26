package eu.codetopic.anty.ev3projectsandroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

import eu.codetopic.utils.ui.activity.navigation.NavigationActivity;

public class MainActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        if (navView != null) {
            navView.setItemTextColor(ContextCompat.getColorStateList(this, android.R.color.white));
            navView.setItemIconTintList(ContextCompat.getColorStateList(this, android.R.color.white));
        }
    }

    @Override
    protected Class<? extends Fragment> getMainFragmentClass() {
        return super.getMainFragmentClass();// TODO: 26.9.16 implement
    }

    @Override
    protected boolean onUpdateSelectedNavigationMenuItem(@Nullable Fragment currentFragment, Menu menu) {
        if (currentFragment == null)
            return super.onUpdateSelectedNavigationMenuItem(null, menu);

        //Class<? extends Fragment> fragmentClass = currentFragment.getClass();

        /*if (fragmentClass.equals(Fragment.class)) {
            menu.findItem(R.id.nav_item).setChecked(true);
            return true;
        }*/

        return super.onUpdateSelectedNavigationMenuItem(currentFragment, menu);
    }

    @Override
    protected boolean onCreateNavigationMenu(Menu menu) {
        super.onCreateNavigationMenu(menu);
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        if (BuildConfig.DEBUG) {
            menu.findItem(R.id.nav_debug).setVisible(true);
        }
        return true;// TODO: 26.9.16 on first time return false and after connect invalidateNavMenu and return true
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //int id = item.getItemId();

        /*if (id == R.id.nav_some_item) {
            replaceFragment(SomeFragment.class);
            return true;
        } else if (id == R.id.nav_some_item) {
            replaceFragment(SomeFragment.class);
            return true;
        }*/

        return super.onNavigationItemSelected(item);
    }

    @Override
    protected void onBeforeReplaceFragment(FragmentTransaction ft, Fragment fragment) {
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
    }
}
