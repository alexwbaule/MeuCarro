package com.alexwbaule.flexprofile.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.containers.DrawerItem;
import com.alexwbaule.flexprofile.containers.Vehicles;
import com.alexwbaule.flexprofile.dialogs.ChangeLog;
import com.alexwbaule.flexprofile.fragments.FuelCharge;
import com.alexwbaule.flexprofile.fragments.FuelChargeAdd;
import com.alexwbaule.flexprofile.fragments.MaintenanceAdd;
import com.alexwbaule.flexprofile.fragments.PreferencesOpen;
import com.alexwbaule.flexprofile.fragments.Vehicle;
import com.alexwbaule.flexprofile.fragments.VehicleAdd;
import com.alexwbaule.flexprofile.services.UpdateAssets;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, Vehicle.OnSetDefaultCarListener,
        VehicleAdd.OnSetDefaultCarListener, PreferencesOpen.OnSetChangePreferenceListener,
        FuelChargeAdd.OnChargeComplete, FuelCharge.OnDeleteChargeListener, MaintenanceAdd.OnMaintenanceComplete {
    private static final String TAG = "MainActivity";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawerLayout;
    private int mNavItemId;
    private MeuCarroApplication rootapp = MeuCarroApplication.getInstance();
    private Vehicles Car;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState = true;
    private NavigationView navigationView;
    private TextView txtCarName;
    private LinearLayout mini_combtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        drawerLayout = findViewById(R.id.drawer_layout);
        Car = rootapp.fetchOneCar(rootapp.getDefaultCar());

        if (!getLastLang().equals(Locale.getDefault().toString())) {
            setLastLang(Locale.getDefault().toString());
            rootapp.updateDescriptionsDB();
        }
        mUserLearnedDrawer = getDrawerLearn();

        Toolbar toolbar = findViewById(R.id.main_toolbar);

        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        getPermission(this, navigationView);


        setHeaderConsumos(Car);

        View header = navigationView.getHeaderView(0);

        txtCarName = header.findViewById(R.id.tSelectCar);
        txtCarName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu menu = navigationView.getMenu();
                if (rootapp.getHasCars() > 0) {
                    if (menu.getItem(0).getGroupId() == R.id.menu_normal) {
                        buildCarMenu(menu, navigationView);
                    } else if (menu.getItem(0).getGroupId() == R.id.car_selection) {
                        buildNormalMenu(menu, navigationView, 0);
                    }
                }
            }
        });

        if (savedInstanceState == null) {
            openFragment(R.id.navigation_calculator);
            mFromSavedInstanceState = false;
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(getClass().getSimpleName(), "MainActivity.onDrawerClosed()");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.d(getClass().getSimpleName(), "MainActivity.onDrawerOpened()");
                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    setDrawerLearn();
                }
            }
        };
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(getClass().getSimpleName(), "Clicou");
            }
        });
        mDrawerToggle.syncState();

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            drawerLayout.openDrawer(navigationView);
        }

        if (!getShowWelcome()) {
            startActivity(new Intent(MainActivity.this, WelcomeScreenActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        ChangeLog cl = new ChangeLog(this);
        if (cl.firstRun())
            cl.getLogDialog().show();

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Log.d(getClass().getSimpleName(), "MeuCarroApplication.startService()");
            startService(new Intent(this, UpdateAssets.class));
        }else {
            Log.d(getClass().getSimpleName(), "MeuCarroApplication.startForegroundService()");
            startForegroundService(new Intent(this, UpdateAssets.class));
        }
/*
        if (rootapp.needsAuthentication){
            Log.d(TAG, "Not logged in");
            Intent signInIntent = rootapp.googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 101);
        }

 */
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setHeaderConsumos(Vehicles Car) {
        View header = navigationView.getHeaderView(0);

        mini_combtv = header.findViewById(R.id.header_gasolina);
        mini_combtv.setVisibility(View.INVISIBLE);
        mini_combtv = header.findViewById(R.id.header_Etanol);
        mini_combtv.setVisibility(View.INVISIBLE);
        mini_combtv = header.findViewById(R.id.header_gnv);
        mini_combtv.setVisibility(View.INVISIBLE);
        mini_combtv = header.findViewById(R.id.header_diesel);
        mini_combtv.setVisibility(View.INVISIBLE);

        if (rootapp.getHasCars() > 0) {
            mini_combtv = header.findViewById(R.id.header_gasolina);
            if (Car.getGasolina() != null) {
                mini_combtv.setVisibility(View.VISIBLE);
            }
            mini_combtv = header.findViewById(R.id.header_Etanol);
            if (Car.getEtanol() != null) {
                mini_combtv.setVisibility(View.VISIBLE);
            }
            mini_combtv = header.findViewById(R.id.header_gnv);
            if (Car.getGnv() != null) {
                mini_combtv.setVisibility(View.VISIBLE);
            }
            mini_combtv = header.findViewById(R.id.header_diesel);
            if (Car.getDiesel() != null) {
                mini_combtv.setVisibility(View.VISIBLE);
            }

            ImageView t = header.findViewById(R.id.imgUserPhoto);
            t.setImageBitmap(Car.getCarImage());
            TextView txt1 = header.findViewById(R.id.txt_nav_gasolina);
            txt1.setText(Car.getGasolina());
            TextView txt2 = header.findViewById(R.id.txt_nav_Etanol);
            txt2.setText(Car.getEtanol());
            TextView txt3 = header.findViewById(R.id.txt_nav_gnv);
            txt3.setText(Car.getGnv());
            TextView txt4 = header.findViewById(R.id.txt_nav_diesel);
            txt4.setText(Car.getDiesel());

            txtCarName = header.findViewById(R.id.tSelectCar);
            txtCarName.setText(Car.getProfile_name());

            setToolBarSubTitle(Car.getProfile_name());
        }
    }


    private void buildNormalMenu(Menu menu, NavigationView navview, int menu_id) {
        menu.clear();
        navview.inflateMenu(R.menu.drawer);
        txtCarName = findViewById(R.id.tSelectCar);
        txtCarName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_header_down, 0);
        if (menu_id == 0)
            menu_id = reloadFragment();
        menu = navview.getMenu();
        for (int j = 0; j < menu.size(); j++) {
            if (menu.getItem(j).getItemId() == menu_id) {
                menu.getItem(j).setCheckable(true).setChecked(true);
            }
        }
    }

    private void buildCarMenu(Menu menu, NavigationView navview) {
        menu.clear();
        navview.inflateMenu(R.menu.car_selection);
        int z = 0;
        for (DrawerItem item : rootapp.fetchCarsName()) {
            boolean check = false;
            if (item.getId() == rootapp.getDefaultCar()) {
                check = true;
            }
            menu.add(R.id.car_selection, (int) item.getId(), z, item.getName()).setIcon(R.drawable.ic_menu_car).setCheckable(true).setChecked(check);
            z++;
        }
        menu.add(R.id.car_selection, 0, z, R.string.add_new_vehicle).setIcon(R.drawable.ic_menu_car_add).setCheckable(true);

        txtCarName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_header_up, 0);
    }

    public void logOutGoogleAccount(){
        rootapp.googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //On Succesfull signout we navigate the user back to LoginActivity
                //Intent intent=new Intent(ProfileActivity.this,MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intent);
            }
        });
    }

    private void revokeAccess() {
        rootapp.googleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    public void updateCarSelect() {
        Car = rootapp.fetchOneCar(rootapp.getDefaultCar());
        setHeaderConsumos(Car);
    }

    public void reloadNormalMenu(int car_id) {
        rootapp.setDefaultCar(car_id);
        updateCarSelect();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mNavItemId = menuItem.getItemId();
        navigationView = findViewById(R.id.navigation);
        Menu menu = navigationView.getMenu();

        if (menuItem.getGroupId() == R.id.menu_normal || menuItem.getGroupId() == R.id.menu_config) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Handler handle = new Handler();
            handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    openFragment(mNavItemId);
                }
            }, 300);
        } else if (menuItem.getGroupId() == R.id.car_selection) {
            if (mNavItemId == 0) {
                replaceFragment(new VehicleAdd(), FRGMNT);
                drawerLayout.closeDrawer(GravityCompat.START);
                buildNormalMenu(menu, navigationView, R.id.navigation_vehicle);
            } else {
                reloadNormalMenu(mNavItemId);
                buildNormalMenu(menu, navigationView, 0);
            }
        }
        return true;
    }

    @Override
    public void onDefaultCarSet() {
        updateCarSelect();
    }

    @Override
    public void onChangePreferenceSet(boolean newmedia) {
        Log.d(TAG, "############## onChangePreferenceSet: CALLED ################");
        if (newmedia)
            rootapp.updateConsumo();
        updateCarSelect();
    }

    @Override
    public void onCharge() {
        updateCarSelect();
    }

    @Override
    public void onDeleteChargeSet() {
        updateCarSelect();
    }

    @Override
    public void onMaintenance() {
        updateCarSelect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    Log.d(TAG, "+++++++++++++++++ RECEIVING +++++++++++++++++");

                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        rootapp.googleAccount = task.getResult(ApiException.class);
                        Log.d(TAG, "+++++++++++++++++ DisplayName: "+ rootapp.googleAccount.getDisplayName() +" email: "+ rootapp.googleAccount.getEmail()+ " Token: " + rootapp.googleAccount.getIdToken()+ "+++++++++++++++++");
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.d(TAG, "++++++++++++++ signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
    }
}
