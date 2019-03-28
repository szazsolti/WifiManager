package ro.ms.sapientia.zsolti.wifimanager.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ro.ms.sapientia.zsolti.wifimanager.Communication.MessageSender;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.GetWiFiListFromDeviceArrayList;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.NotifyToDraw;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.Trilateration;
import ro.ms.sapientia.zsolti.wifimanager.WiFi;
import ro.ms.sapientia.zsolti.wifimanager.WifiScanReceiver;


public class SearchWifiFragment extends Fragment implements GetWiFiListFromDeviceArrayList{

    private OnFragmentInteractionListener mListener;
    private Thread refreshWifi = new Thread();
    private NotifyToDraw notifyToDraw;
    private WifiManager mainWifiObj;
    private WifiScanReceiver wifiReciever;
    private ListView list;
    private Context context;
    private String TAG = "SEARCHWIFIFRAGMENT";
    private ArrayList<WiFi> wifiListFromDataBase;


    public SearchWifiFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SearchWifiFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    public static SearchWifiFragment newInstance() {
        SearchWifiFragment fragment = new SearchWifiFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        wifiListFromDataBase = (ArrayList<WiFi>) getArguments().getSerializable("wifilist");

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search_wifi, container, false);
        //tw_test2 = view.findViewById(R.id.tw_test2);
        list=view.findViewById(R.id.list);
        //refreshWifi.start();

        DrawPositionFragment drawPositionFragment = new DrawPositionFragment(context);
        //drawPositionFragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, drawPositionFragment);
        fragmentTransaction.addToBackStack("searchwifi");
        fragmentTransaction.commit();


        if(checkPermission()) {
            mainWifiObj = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            startRefresh();
            wifiReciever = new WifiScanReceiver(mainWifiObj);
            wifiReciever.setGetWiFiListFromDeviceArrayList(this);
            context.registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        }

        //Log.d(TAG,"Wifilist: " + wifiListFromDataBase.get(0).getName());

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        if(checkPermission()) {
            context.registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            mainWifiObj = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            mainWifiObj.startScan();
            startRefresh();
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        super.onDetach();
        context.unregisterReceiver(wifiReciever);
        mListener = null;
    }

    @Override
    public void returnWiFiListFromDevice(ArrayList<WiFi> wifisFromDevice) {
        //Log.d(TAG, "Wifis returned: " + stringArray[0]);
        try{
            calculateTrilateration(wifisFromDevice);
            //notifyToDraw.notifyToDraw("DRAW!");

            //list.setAdapter(new ArrayAdapter<>(context,R.layout.list_item,R.id.label, stringArray));
        }
        catch (Exception e){
            //e.printStackTrace();
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void startRefresh(){
        refreshWifi = new Thread(){
            public void run(){
                while (true){
                    mainWifiObj.startScan();
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        refreshWifi.start();
    }
    public boolean checkPermission(){
        final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
            else if (manager != null && !manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
                Toast.makeText(context, "You need to enable your GPS", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
            else {
                return true;
            }
        }
        return true;
    }


    public void calculateTrilateration(ArrayList<WiFi> wifilistFromDevice){

        //Log.d(TAG,"From phone: "+wifilistFromDevice.get(0).getName()+wifilistFromDevice.get(0).getFrequency()+wifilistFromDevice.get(0).getPercentage());

        ArrayList<WiFi> choosedWifis =  chooseWifis(wifilistFromDevice, wifiListFromDataBase);

        if(choosedWifis!=null){
            //Log.d(TAG,"Sorted 3: " + choosedWifis.toString());
            Trilateration trilateration = Trilateration.getInstance();
            trilateration.setSsidDistance(choosedWifis);
            //Log.d(TAG,"X,Y: " + choosedWifis.get(0).getX()+" "+choosedWifis.get(0).getY()+" "+choosedWifis.get(1).getX()+" "+choosedWifis.get(1).getY()+" "+choosedWifis.get(2).getX()+" "+choosedWifis.get(2).getY());
            trilateration.setRouter(choosedWifis.get(0).getX(),choosedWifis.get(0).getY(),choosedWifis.get(1).getX(),choosedWifis.get(1).getY(),choosedWifis.get(2).getX(),choosedWifis.get(2).getY());
           // Log.d(TAG,"Distance: " + choosedWifis.get(0).getDistance());
            //Log.d(TAG,"Percentage: " + choosedWifis.get(0).getPercentage());
            trilateration.setR();
            trilateration.setParameters();
            Log.d(TAG,"X: " + trilateration.getX() + " Y: " + trilateration.getY());
            MessageSender messageSender = new MessageSender();
            messageSender.execute("X: "+trilateration.getX() +" Y:"+ trilateration.getY());
            sendBroadcastNotify();


            //getMessageToDraw.returnMessageToDraw("draw");
        }
        //Log.d(TAG,"Wifis: " + message);
    }

    public void sendBroadcastNotify(){
        Intent intent = new Intent("ro.ms.sapientia.zsolti.draw");
        intent.putExtra("notifyToDraw","Broadcast received");
        context.sendBroadcast(intent);
    }

    public ArrayList<WiFi> chooseWifis(ArrayList<WiFi> wifilistFromDevice, ArrayList<WiFi> wifiListFromDataBase){
        ArrayList<WiFi> choosedWifis = new ArrayList<>();

        choosedWifis.clear();

        for(WiFi databaseList : wifiListFromDataBase){
            for(WiFi deviceList : wifilistFromDevice){
                if(databaseList.getName().equals(deviceList.getName())){
                    choosedWifis.add(calculateDistance(databaseList,deviceList));
                }
            }
        }

        wifilistFromDevice.clear();

        Collections.sort(choosedWifis, new Comparator<WiFi>() {
            @Override
            public int compare(WiFi o1, WiFi o2) {
                if(o1.getPercentage()>o2.getPercentage()){
                    return -1;
                }
                return 1;
            }
        });

        //Log.d(TAG,"Sorted in chooseWifis: " + choosedWifis.toString());

        if(choosedWifis.size()>=3){
            return new ArrayList<>(choosedWifis.subList(0, 3));
        }
        return null;
    }
//transmitter power, dBm
//cable loss at transmitter and receiver, dB ( 0, if no cables )
//cable loss at transmitter and receiver, dB ( 0, if no cables )
//antenna gain at transmitter and receiver, dBi
//antenna gain at transmitter and receiver, dBi
//fade margin, dB ( more than 14 dB (normal) or more than 22 dB (good))

    public WiFi calculateDistance (WiFi wifiDatabase, WiFi wifiDevice){
        Double level = wifiDevice.getLevel();
        Double frequency = wifiDevice.getFrequency();
        double K = -27.55; //mert frekvencia MHz-ben és a távolság m-ben
        double Ptx = 19.0;
        double CLtx = 0.0;
        double CLrx = 0.0;
        double AGtx = 5.0;
        double AGrx = 0.0;
        double FM = 22.0;
        double FSPL =Ptx - CLtx + AGtx + AGrx - CLrx - (level + 10)- FM; // - Free Space Loss
        double dist = round(Math.pow(10.0,((FSPL - K - 20*Math.log10(frequency))/20)),3);
        int percentage = getWifiStrengthPercentage((int)wifiDevice.getLevel());
        wifiDevice.setDistance(dist);
        wifiDevice.setPercentage(percentage);
        wifiDevice.setX(wifiDatabase.getX());
        wifiDevice.setY(wifiDatabase.getY());
        return wifiDevice;
    }

 /*   public double round(double d, int decimalPlace) {
        return Double.parseDouble((d+"").substring(0,((d+"").indexOf(".")+decimalPlace+1)));
    }*/

    public static double round(double d, int decimalPlace) {
        try {
            BigDecimal bigdecimal = new BigDecimal(Double.toString(d));
            bigdecimal = bigdecimal.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
            return bigdecimal.doubleValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getWifiStrengthPercentage(int decibel)
    {
        try
        {
            int level = WifiManager.calculateSignalLevel(decibel, 100);
            return level;
        }
        catch (Exception e)
        {
            return 0;
        }
    }
}
