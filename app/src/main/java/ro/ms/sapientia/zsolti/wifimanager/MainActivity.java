package ro.ms.sapientia.zsolti.wifimanager;

import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;
import ro.ms.sapientia.zsolti.wifimanager.Communication.MessageSender;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.DrawPositionFragment;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.HomeFragment;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.SearchWifiFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.GetMessageListener;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, SearchWifiFragment.OnFragmentInteractionListener, DrawPositionFragment.OnFragmentInteractionListener,GetMessageListener {

    private boolean doubleBackToExitPressedOnce = false;
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HomeFragment homeFragment = new HomeFragment(getApplicationContext());
        homeFragment.setGetMessageListener(this);
        //Manager.getInstance().setContext(getApplicationContext());
        //homeFragment.setGetMessageInFragment(getMessageInFragment);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

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

    @Override
    protected void onStop() {
        super.onStop();
        sendMessage("[Logout]-");
    }

    public void sendMessage(String message){
        MessageSender messageSender = new MessageSender();
        messageSender.setGetMessageListener(this);
        messageSender.execute(message);
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