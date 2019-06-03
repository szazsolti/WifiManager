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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import java.io.IOException;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;
import ro.ms.sapientia.zsolti.wifimanager.Communication.Communication;
import ro.ms.sapientia.zsolti.wifimanager.Communication.MessageSender;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.DrawPositionFragment;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.HomeFragment;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.ListWiFisToSetReference;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.WiFiReferencePointsFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;

public class MainActivity extends AppCompatActivity implements ISendDataToUIListener {

    private boolean doubleBackToExitPressedOnce = false;
    private String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //myToolbar = findViewById(R.id.toolBar);
        //setSupportActionBar(myToolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.trilateration:
                        //menuItem.setChecked(true);
                        startTrilateration();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.save_as_reference:
                        //menuItem.setChecked(true);
                        startListWifisToSetReference();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.reference_points:
                        //menuItem.setChecked(true);
                        startWiFiReferencePoints();
                        drawerLayout.closeDrawers();
                        return true;
                }
                return false;
            }
        });

        if(checkPermission()){
            HomeFragment homeFragment = new HomeFragment(getApplicationContext());
            homeFragment.setISendDataToUIListener(this);
            //Manager.getInstance().setContext(getApplicationContext());
            //homeFragment.setISendMessageFromReaderThreadToHomeFragment(getMessageInFragment);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, homeFragment);
            fragmentTransaction.commit();
        }

    }

    public void startTrilateration(){
        DrawPositionFragment drawPositionFragment = new DrawPositionFragment(WiFiManagerSuperClass.getContext());
        //drawPositionFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();;
        //drawPositionFragment.setISendDataToUIListener(sendDataToUIListener);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, drawPositionFragment);
        fragmentTransaction.addToBackStack("WiFiReferencePointsFragment");
        fragmentTransaction.commit();
    }

    public void startListWifisToSetReference(){
        ListWiFisToSetReference searchWifiFragment = new ListWiFisToSetReference(this);
        //searchWifiFragment.setArguments(bundle);
        //searchWifiFragment.setNotifyToDraw(notifyDraw);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, searchWifiFragment);
        fragmentTransaction.addToBackStack("drawPositionFragment");
        fragmentTransaction.commit();
    }

    public void startWiFiReferencePoints(){
        WiFiReferencePointsFragment wiFiReferencePointsFragment = new WiFiReferencePointsFragment(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, wiFiReferencePointsFragment);
        fragmentTransaction.addToBackStack("drawPositionFragment");
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
                if(text.contains("Socket is closed.")){
                    startHomeFragment();
                    Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startHomeFragment(){
        HomeFragment homeFragment = new HomeFragment(getApplicationContext());
        homeFragment.setISendDataToUIListener(this);
        //Manager.getInstance().setContext(getApplicationContext());
        //homeFragment.setISendMessageFromReaderThreadToHomeFragment(getMessageInFragment);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart:");

        try {
            //if(Communication.getInstance().isExists()){
            //Communication.getInstance().destroy();
            Communication.getInstance().sendUsername();

            //}
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        try {
            MessageSender messageSender = new MessageSender();
            messageSender.execute("[Username]-"+Client.getInstance().getUsername());
        } catch (IOException e) {

            Toast.makeText(getApplicationContext(),"Failed to connect to server.",Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            Communication.getInstance().sendMessage("[Logout]-");
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        Thread logout = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "run: onStop called");
                    if(Communication.getInstance().connected()){
                        Communication.getInstance().sendMessage("[Logout]-");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        logout.start();*/
        //stopService(new Intent(getBaseContext(),SocketService.class));
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
                Toast.makeText(getApplicationContext(), "You need to enable your WIFI.", Toast.LENGTH_SHORT).show();
                return false;
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
        try {
            //Client.getInstance().destroy();
            Communication.getInstance().destroy();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}