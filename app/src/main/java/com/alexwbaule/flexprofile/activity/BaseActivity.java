package com.alexwbaule.flexprofile.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.dialogs.DialogDeniedPermission;
import com.alexwbaule.flexprofile.fragments.Calculator;
import com.alexwbaule.flexprofile.fragments.FuelCharge;
import com.alexwbaule.flexprofile.fragments.Help;
import com.alexwbaule.flexprofile.fragments.Maintenance;
import com.alexwbaule.flexprofile.fragments.OilCharge;
import com.alexwbaule.flexprofile.fragments.PreferencesOpen;
import com.alexwbaule.flexprofile.fragments.Report;
import com.alexwbaule.flexprofile.fragments.Vehicle;

/**
 * Created by alex on 07/07/15.
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String PREF_USER_LAST_LANG = "phone_last_lang";
    private static final String WELCOME_SHOWED = "welcome_showed";


    public static final int MY_PERMISSION_LIST = 25;


    protected static final String FRGMNT = "frgmnt";

    static {
        // Para usar isso, precisa habilitar o tema DayNight, porem esta com bug (02/05/2016)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    private void getNightMode() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                Log.i(TAG, "############# NIGHT MODE OFF ITS DAY ##################");
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                Log.i(TAG, "############# NIGHT MODE ON ITS NIGHT ##################");
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                Log.i(TAG, "############# NIGHT MODE UNDEFINED SO ITS DAY ##################");
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().applyDayNight();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getNightMode();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        Log.d(getClass().getSimpleName(), "BaseActivity.onCreate()");
    }

    protected void replaceFragment(Fragment frag, String tag) {
        if (frag instanceof PreferencesOpen) {
            getSupportFragmentManager().popBackStackImmediate();
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_drawer_container, frag, tag).addToBackStack(tag).commit();
        } else {
            getSupportFragmentManager().popBackStackImmediate();
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_drawer_container, frag, tag).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0) {
            switch (requestCode) {
                case MY_PERMISSION_LIST: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(getClass().getSimpleName(), "OK, ACCESS");
                        //mCallback.updatePreference(true);
                    } else {
                        Log.d(getClass().getSimpleName(), "DENIED, ACCESS");
                        //mCallback.updatePreference(false);
                    }
                }
            }
        }
    }

    protected void getPermission(final AppCompatActivity activity, View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    DialogDeniedPermission dialog = new DialogDeniedPermission();
                    dialog.show(getSupportFragmentManager(), "PERMISSION");
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_LIST);
                }
            }
        }
    }

    protected int reloadFragment() {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(FRGMNT);
        int menu_id = 0;
        if (frag instanceof Calculator) {
            menu_id = R.id.navigation_calculator;
            openFragment(R.id.navigation_calculator);
        } else if (frag instanceof FuelCharge) {
            menu_id = R.id.navigation_fuel;
            openFragment(R.id.navigation_fuel);
        } else if (frag instanceof OilCharge) {
            menu_id = R.id.navigation_oil;
            openFragment(R.id.navigation_oil);
        } else if (frag instanceof Vehicle) {
            menu_id = R.id.navigation_vehicle;
            openFragment(R.id.navigation_vehicle);
        } else if (frag instanceof Maintenance) {
            menu_id = R.id.navigation_vehicle_manut;
            openFragment(R.id.navigation_vehicle_manut);
        } else if (frag instanceof Report) {
            menu_id = R.id.navigation_report;
            openFragment(R.id.navigation_report);
        } else if (frag instanceof PreferencesOpen) {
            menu_id = R.id.navigation_config;
        } else if (frag instanceof Help) {
            menu_id = R.id.navigation_help;
        }
        return menu_id;
    }


    protected void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected void setToolBarSubTitle(String txt) {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        if (toolbar != null) {
            toolbar.setSubtitle(txt);
        }
    }

    protected void openFragment(int position) {
        Toolbar tb = findViewById(R.id.main_toolbar);
        tb.getMenu().clear();
        //tb.setSubtitle(null);

        Log.d(getClass().getSimpleName(), "OpenFragment Yeap");

        switch (position) {
            case R.id.navigation_calculator:
                replaceFragment(new Calculator(), FRGMNT);
                break;
            case R.id.navigation_fuel:
                replaceFragment(new FuelCharge(), FRGMNT);
                break;
            case R.id.navigation_oil:
                replaceFragment(new OilCharge(), FRGMNT);
                break;
            case R.id.navigation_vehicle:
                replaceFragment(new Vehicle(), FRGMNT);
                break;
            case R.id.navigation_report:
                replaceFragment(new Report(), FRGMNT);
                break;
            case R.id.navigation_vehicle_manut:
                replaceFragment(new Maintenance(), FRGMNT);
                break;
            case R.id.navigation_config:
                replaceFragment(new PreferencesOpen(), FRGMNT);
                break;
            case R.id.navigation_help:
                replaceFragment(new Help(), FRGMNT);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Log.d(getClass().getSimpleName(), "BaseActivity.onBackPressed()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getSimpleName(), "BaseActivity.onDestroy()");
    }

    protected boolean getDrawerLearn() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
    }

    protected void setDrawerLearn() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
    }

    protected void setLastLang(String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putString(PREF_USER_LAST_LANG, value).apply();
    }

    protected String getLastLang() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getString(PREF_USER_LAST_LANG, "pt_BR");
    }

    protected boolean getShowWelcome() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getBoolean(WELCOME_SHOWED, false);
    }

    protected void setHideWelcome() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(WELCOME_SHOWED, true).apply();
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof PreferencesOpen) {
            try {
                //mCallback = (OnSetupdatePreferences) fragment;
            } catch (ClassCastException e) {
                throw new ClassCastException(fragment.toString() + " must implement OnSetupdatePreferences");
            }
        }
    }
}
