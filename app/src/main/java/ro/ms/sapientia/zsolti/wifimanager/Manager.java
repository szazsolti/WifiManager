
package ro.ms.sapientia.zsolti.wifimanager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Communication;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.DrawPositionFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromWifiScanReceiverToManager;

public class Manager implements ISendWiFiListFromWifiScanReceiverToManager, Runnable{

    private static Manager sinlge_instance = null;
    private String TAG = "MANAGER";
    private ArrayList<WiFi> wifiListFromDataBase;
    private ArrayList<WiFi> wifiListFromDevice;
    private WifiManager mainWifiObj;
    private WifiScanReceiver wifiReciever;
    private Thread refreshWifi = new Thread();
    //private NotifyToDraw notifyToDraw;
    private Thread refreshData;
    private Context context;
    private FragmentManager fragmentManager;
    private ISendDataToUIListener sendDataToUIListener;


    private Manager(Context context){
        this.context = context;
        //if(checkPermission()) {
            mainWifiObj = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            startRefresh();
            wifiReciever = new WifiScanReceiver(mainWifiObj);
            wifiReciever.setISendWiFiListFromDeviceArrayListFromWifiScanReceiverToManager(this);
            context.registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //}
    }

    public static void init(Context context) {
        if (sinlge_instance == null) {
            synchronized (Manager.class) {
                if (sinlge_instance == null) {
                    sinlge_instance = new Manager(context.getApplicationContext());
                }
            }
        }
    }

    public static Manager getInstance(){
        /*if (sinlge_instance == null) {
            synchronized (Manager.class) {
                if (sinlge_instance == null) {
                    sinlge_instance = new Manager();
                }
            }
        }*/
        return sinlge_instance;
    }

    @Override
    public void returnWiFiListFromDevice(ArrayList<WiFi> wifisFromDevice) {
        //Log.d(TAG, "Wifis returned: " + stringArray[0]);

        //Log.d(TAG, "returnWiFiListFromDevice: "+wifisFromDevice.toString());
        this.wifiListFromDevice=new ArrayList<>();
        this.wifiListFromDevice=wifisFromDevice;
        Log.d(TAG, "returnWiFiListFromDeviceInManager: " + wifiListFromDevice.toString());
        try{
            calculateTrilateration(wifisFromDevice);
        }
        catch (Exception e){
            //e.printStackTrace();
        }
    }

    public void setWifiListFromDataBase(ArrayList<WiFi> wifis){
        this.wifiListFromDataBase = wifis;
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
/*
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
                context.startActivity(myIntent);
            }
            else {
                return true;
            }
        }
        return true;
    }*/

    public void calculateTrilateration(ArrayList<WiFi> wifilistFromDevice) throws IOException {

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
            /*
            MessageSender messageSender = new MessageSender();
            messageSender.execute("[Position]-"+trilateration.getX() +" "+ trilateration.getY());*/

            Communication.getInstance().sendMessage("[Position]-"+trilateration.getX() +" "+ trilateration.getY());
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

    public void setFragmentManager(FragmentManager fragmentManager){
        this.fragmentManager=fragmentManager;
    }

    public ArrayList<WiFi> getWifisFromDevice(){
        return this.wifiListFromDevice;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        DrawPositionFragment drawPositionFragment = new DrawPositionFragment(context);
        //drawPositionFragment.setArguments(bundle);
        //FragmentManager fragmentManager = fragmentManager;
        drawPositionFragment.setISendDataToUIListener(sendDataToUIListener);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, drawPositionFragment);
        fragmentTransaction.addToBackStack("searchwifi");
        fragmentTransaction.commit();

    }
    public void setISendDataToUIListener(ISendDataToUIListener ISendDataToUIListener){
        this.sendDataToUIListener = ISendDataToUIListener;
    }
}