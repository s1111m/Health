package com.relsib.application;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.relsib.helper.FontsOverride;

//import android.support


public class MainActivityView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static String TAG = MainActivityView.class.getSimpleName();
    public static BLEService mBLEService;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            mBLEService = ((BLEService.LocalBinder) service).getService();
            if (!mBLEService.initialize(MainActivityView.this)) {
                //  Log.e(TAG, "Unable to initialize Bluetooth");
            }
            //Log.e(TAG, "onService connected");
            mBLEService.loadMyThermometers();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // mBLEService = null;
        }
    };
    Fragment currentFragment;
    NavigationView navigationView;
    DrawerLayout drawer;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getFragmentManager().beginTransaction().addToBackStack(null).add(R.id.frgmCont, MyDevicesListView.newInstance("test", "test")).commit();
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().addToBackStack(null).add(R.id.frgmCont, MyDevicesListView.newInstance("test", "test")).commit();
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Roboto-Bold.ttf");
        //register service
        Log.e(TAG, "APP_START");
//        new Thread(new Runnable() {
//            public void run() {
                Intent gattServiceIntent = new Intent(getApplicationContext(), BLEService.class);
                bindService(gattServiceIntent, mServiceConnection, Activity.BIND_AUTO_CREATE);
        //       }
//        }).start();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

//        getFragmentManager().beginTransaction().replace(R.id.frgmCont, MyDevicesListView.newInstance("tesy", "test")).commit();
        //getFragmentManager().beginTransaction().addToBackStack(null).add(R.id.frgmCont, MyDevicesListView.newInstance("test", "test")).commit();
//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mBLEService.pushUnknownToMyDevices();
//                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frgmCont, MyDevicesListView.newInstance("tesy", "test")).commit();
//                //    for (int i = 0; i < BLEService.thermometers.size(); i++) {
//                //   BLEService.thermometers.get(i).connect(true);
//                    //   mBLEService.thermometers.get(i).getTemperatureByNotify(true);
//                // }
//
//
//            }
//        });
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            BLEService.clear();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Class fragmentClass;
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_calendar:
                fragmentClass = MyDevicesListView.class;
                //fragmentClass = MeasureCalendarView.class;
                break;
//            case R.id.nav_charts:
//                fragmentClass = MeasureChartView.class;
//                break;
//            case R.id.nav_list:
//                fragmentClass = MeasureListView.class;
//                break;
            case R.id.nav_thermometers:
                fragmentClass = DeviceSearchView.class;
                break;
//            case R.id.nav_alarms:
//                fragmentClass = AlarmsView.class;
//                break;
//            case R.id.nav_settings:
//                fragmentClass = SettingsViewAlarm.class;
//                break;
            default:
                fragmentClass = MyDevicesListView.class;
                break;
        }
        try {
            currentFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (id == R.id.nav_calendar) {
            getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frgmCont, MyDevicesListView.newInstance("tesy", "test")).commit();
        } else {
            getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frgmCont, currentFragment).commit();
        }
        navigationView.setCheckedItem(id);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}