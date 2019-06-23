package ro.ms.sapientia.zsolti.wifimanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Inflater;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;
import ro.ms.sapientia.zsolti.wifimanager.Communication.Communication;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.DrawPositionFragmentI;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.HomeFragment;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.ListWiFisToSetReferenceFragment;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.SettingsFragment;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.WiFiReferencePointsFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.IDrawerLocker;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromHomefragmentToMainActivity;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromManagerToMainActivity;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromManagerToWiFiReferencePointsFragment;

public class MainActivity extends AppCompatActivity implements ISendDataToUIListener, IDrawerLocker, ISendMessageFromManagerToMainActivity, ISendMessageFromHomefragmentToMainActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private NavigationView navigationView;
    private Thread managerThread;
    private String TAG = "MainActivity";
    private DrawerLayout drawerLayout;

    static final String USERNAME = "username";
    private DrawPositionFragmentI drawPositionFragment;
    private ListWiFisToSetReferenceFragment listWiFisToSetReferenceFragment;
    private WiFiReferencePointsFragment wiFiReferencePointsFragment;
    private SettingsFragment settingsFragment;
    private TextView tw_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);

        View headerView = getLayoutInflater().inflate(R.layout.header_main,null);

        navigationView.addHeaderView(headerView);

        tw_username = headerView.findViewById(R.id.tw_username);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.trilateration:
                        startTrilateration();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.save_as_reference:
                        startListWifisToSetReference();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.reference_points:
                        startWiFiReferencePoints();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.settings:
                        startSettingsFragment();
                        drawerLayout.closeDrawers();
                        return true;
                }
                return false;
            }
        });


        if(checkPermission()){
            if (savedInstanceState != null){
                //Manager.getInstance().startCommunication();
                Communication.getInstance().execute();
                startManager();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Communication.getInstance().sendUsername();
                    }
                });
                thread.start();
                startHomeFragment();
            }
            else {

                startManager();
                startHomeFragment();
            }

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: " + Client.getInstance().getUsername());
        outState.putString(USERNAME, Client.getInstance().getUsername());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: ");
        Client.getInstance().setUsername(savedInstanceState.getString(USERNAME));
        startHomeFragment();
    }

    public void startTrilateration(){
        if(drawPositionFragment==null){
            drawPositionFragment = new DrawPositionFragmentI(this);
        }
        //drawPositionFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();;
        //drawPositionFragment.setISendDataToUIListener(sendDataToUIListener);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, drawPositionFragment);
        //fragmentTransaction.addToBackStack("mainActivity1");
        fragmentTransaction.commit();
    }

    public void startListWifisToSetReference(){
        if(listWiFisToSetReferenceFragment ==null){
            listWiFisToSetReferenceFragment = new ListWiFisToSetReferenceFragment(this);
            //listWiFisToSetReferenceFragment.setReferencePointsFromDatabaseList(new ArrayList<ReferencePoint>());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, listWiFisToSetReferenceFragment);
            //fragmentTransaction.addToBackStack("mainActivity3");
            fragmentTransaction.commit();

        }
        else{
            //listWiFisToSetReferenceFragment.setArguments(bundle);
            //listWiFisToSetReferenceFragment.setNotifyToDraw(notifyDraw);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, listWiFisToSetReferenceFragment);
            //fragmentTransaction.addToBackStack("mainActivity2");
            fragmentTransaction.commit();
        }
    }

    public void startWiFiReferencePoints(){
        if(wiFiReferencePointsFragment==null){/*
            try {
                Communication.getInstance().sendMessage("[ReferenceWifiPointsFromDatabase]-");
            } catch (IOException ignored) {
                Toast.makeText(getApplicationContext(),
                        "Failed to send message from StartWiFiReferencePoints.",
                        Toast.LENGTH_SHORT).show();
            }*/
            wiFiReferencePointsFragment = new WiFiReferencePointsFragment(this);
            wiFiReferencePointsFragment.setReferencePointsFromDatabaseList(new ArrayList<ReferencePoint>());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, wiFiReferencePointsFragment);
            //fragmentTransaction.addToBackStack("mainActivity3");
            fragmentTransaction.commit();
        }
        else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, wiFiReferencePointsFragment);
            //fragmentTransaction.addToBackStack("mainActivity3");
            fragmentTransaction.commit();
        }
    }

    public void startSettingsFragment(){
        if(settingsFragment==null){
            settingsFragment = new SettingsFragment(this);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, settingsFragment);
        //fragmentTransaction.addToBackStack("mainActivity2");
        fragmentTransaction.commit();
    }


    @Override
    public void returnMessage(final String text) {
        //processingPachet(text);
        //getMessageInFragment.returnMessage(text);
        Log.d(TAG,"Message: "+ text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startManager(){
        Manager manager = Manager.getInstance();
        //manager.setWifiListFromDataBase(wifiList);
        manager.setFragmentManager(getSupportFragmentManager());
        manager.setISendDataToUIListener(this);
        //manager.startCommunication();
        manager.setISendMessageFromManagerToMainActivity(this);
        //manager.setISendWiFiListFromManagerToWiFiReferencePointsFragment(sendWiFiListFromManagerToWiFiReferencePointsFragment);
        managerThread = new Thread(manager);
        managerThread.start();

    }

    private void startHomeFragment(){
        HomeFragment homeFragment = new HomeFragment(getApplicationContext());
        homeFragment.setISendDataToUIListener(this);
        homeFragment.setISendMessageFromHomeFragmentToMainActivity(this);
        //Manager.getInstance().setContext(getApplicationContext());
        //homeFragment.setISendMessageFromReaderThreadToHomeFragment(getMessageInFragment);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
/*
            Thread sendUsername = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Communication.getInstance().sendMessage("[Username]-" + Client.getInstance().getUsername());
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            });
            sendUsername.start();*/
        //startHomeFragment();
    }


    @Override
    protected void onPause() {
        super.onPause();
/*
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(Communication.getInstance().connected() && Communication.getInstance() != null){
                        Communication.getInstance().sendMessage("[Logout]-");
                        Manager.getInstance().stopManager();
                        Communication.getInstance().destroy();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();*/
    }

    public boolean checkPermission(){
        DetectInternetConnection detectInternetConnection = new DetectInternetConnection(getApplicationContext());
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
            else if (manager != null && !manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
                Toast.makeText(getApplicationContext(), "You need to enable your GPS.", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
            else if(!detectInternetConnection.isConnectingToInternet()){
                Toast.makeText(getApplicationContext(), "You need to enable your Wi-Fi.", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(myIntent);
            }
            else {
                return true;
            }
        }
        return true;
    }
/*
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Kilépéshez nyomja meg mégegyszer a vissza gombot!", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy()");

        Manager.getInstance().stopManager();
        Communication.getInstance().destroy();
    }

    @Override
    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawerLayout.setDrawerLockMode(lockMode);
    }

    @Override
    public void messageFromManagerToMainActivity(ArrayList<ReferencePoint> referencePointsFromDatabase) {
        if(wiFiReferencePointsFragment==null){
            wiFiReferencePointsFragment = new WiFiReferencePointsFragment(this);
            wiFiReferencePointsFragment.setISendDataToUIListener(this);
        }
        //Log.d(TAG, "messageFromManagerToMainActivity: " + referencePointsFromDatabase.size());
        wiFiReferencePointsFragment.setReferencePointsFromDatabaseList(referencePointsFromDatabase);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, wiFiReferencePointsFragment);
        fragmentTransaction.addToBackStack("mainActivity3");
        fragmentTransaction.commit();
    }


    @Override
    public void setMessageFromHomeFragmentToMainActivity(String message) {
        tw_username.setText(message);
    }
}