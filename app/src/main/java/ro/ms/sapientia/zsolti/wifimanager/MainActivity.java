package ro.ms.sapientia.zsolti.wifimanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;
import ro.ms.sapientia.zsolti.wifimanager.Communication.MessageSender;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.HomeFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;

public class MainActivity extends AppCompatActivity implements ISendDataToUIListener {

    private boolean doubleBackToExitPressedOnce = false;
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    @Override
    public void returnMessage(final String text) {
        //processingPachet(text);
        //getMessageInFragment.returnMessage(text);
        Log.d(TAG,"Message: "+ text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(text.equals("Socket is closed.")){
                    //startHomeFragment();
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
    protected void onStop() {
        super.onStop();
        sendMessage("[Logout]-");
        //stopService(new Intent(getBaseContext(),SocketService.class));
    }

    public void sendMessage(String message){
        MessageSender messageSender = new MessageSender();
        messageSender.setISendDataToUIListener(this);
        messageSender.execute(message);
    }

    public boolean checkPermission(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
            else if (manager != null && !manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
                Toast.makeText(getApplicationContext(), "You need to enable your GPS", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
            else {
                return true;
            }
        }
        return true;
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Client.getInstance().destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}