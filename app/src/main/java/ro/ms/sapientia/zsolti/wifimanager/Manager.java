
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
import java.util.Random;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;
import ro.ms.sapientia.zsolti.wifimanager.Communication.Communication;
import ro.ms.sapientia.zsolti.wifimanager.Fragments.DrawPositionFragmentI;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromManagerToMainActivity;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromManagerToWiFiReferencePointsFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromReaderThreadToManager;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromManagerToWiFiReferencePointsFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromWifiScanReceiverToManager;

public class Manager implements ISendWiFiListFromWifiScanReceiverToManager, Runnable, ISendMessageFromReaderThreadToManager {

    private static Manager sinlge_instance = null;
    private String TAG = "MYMANAGER";
    private ArrayList<WiFi> wifiListFromDataBase = new ArrayList<>();
    private ArrayList<WiFi> wifiListFromDevice = new ArrayList<>();
    private ArrayList<UserOnCanvas> onlineUsers = new ArrayList<>();
    private ArrayList<ReferencePoint> referencePointsFromDatabase = new ArrayList<>();
    private WifiManager mainWifiObj;
    private WifiScanReceiver wifiReciever;
    private Thread refreshWifi = new Thread();
    private Thread startConnection;
    private ISendMessageFromManagerToMainActivity sendMessageFromManagerToMainActivity;
    private ISendMessageFromManagerToWiFiReferencePointsFragment sendMessageFromManagerToWiFiReferencePointsFragment;
    private Random random = new Random();
    private FragmentManager fragmentManager;
    private ISendDataToUIListener sendDataToUIListener;
    private ISendMessageFromReaderThreadToManager sendMessageFromReaderThreadToManager = Manager.this;

    private Manager(){
    }

    private void inintWifiScanReceiver(){
        mainWifiObj = (WifiManager) WiFiManagerSuperClass.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        startRefresh();
        if(wifiReciever==null){
            wifiReciever = new WifiScanReceiver(mainWifiObj);
        }
        wifiReciever.setISendWiFiListFromDeviceArrayListFromWifiScanReceiverToManager(Manager.this);
        WiFiManagerSuperClass.getContext().registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public static Manager getInstance(){
        if (sinlge_instance == null) {
            synchronized (Manager.class) {
                if (sinlge_instance == null) {
                    sinlge_instance = new Manager();
                }
            }
        }
        return sinlge_instance;
    }


    public void stopManager(){
        try{
            WiFiManagerSuperClass.getContext().unregisterReceiver(wifiReciever);
        }catch (Exception e){}

        refreshWifi=null;
        startConnection=null;
    }

    @Override
    public void returnWiFiListFromDevice(ArrayList<WiFi> wifilistFromDevice) {
        this.wifiListFromDevice=new ArrayList<>();
        this.wifiListFromDevice=wifilistFromDevice;

        try{
            calculateTrilateration(new ArrayList<>(wifilistFromDevice));
        }
        catch (Exception e){
        }
    }

    public void startRefresh(){
        refreshWifi = new Thread(){
            public void run(){
                String hashCode=Communication.getInstance().hashCode()+"";
                while (true){
                    try {
                        if(Communication.getInstance()==null || Communication.getInstance().isCancelled() || !hashCode.equals(Communication.getInstance().hashCode()+"")){
                            break;
                        }
                        mainWifiObj.startScan();
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        refreshWifi.start();
    }

    public void calculateTrilateration(ArrayList<WiFi> wifilistFromDevice){
        Log.d(TAG, "calculateTrilateration: ");
        ArrayList<WiFi> choosedWifis =  chooseWifis(wifilistFromDevice, wifiListFromDataBase);

        if(choosedWifis!=null){
            Trilateration trilateration = Trilateration.getInstance();
            trilateration.setSsidDistance(choosedWifis);
            trilateration.setRouter(choosedWifis.get(0).getX(),choosedWifis.get(0).getY(),choosedWifis.get(1).getX(),choosedWifis.get(1).getY(),choosedWifis.get(2).getX(),choosedWifis.get(2).getY());
            trilateration.setR();
            trilateration.setParameters();
            Log.d(TAG,"X: " + trilateration.getX() + " Y: " + trilateration.getY());
            Client.getInstance().setXTrilat(trilateration.getX()+"");
            Client.getInstance().setYTrilat(trilateration.getY()+"");
            Communication.getInstance().sendMessage("[PositionTrilat]-"+trilateration.getX() +" "+ trilateration.getY());
            sendBroadcastNotify();
        }
    }

    public void sendBroadcastNotify(){
        Intent intent = new Intent("ro.ms.sapientia.zsolti.draw");
        intent.putExtra("INotifyToDraw","Broadcast received");
        WiFiManagerSuperClass.getContext().sendBroadcast(intent);
    }

    public ArrayList<WiFi> chooseWifis(ArrayList<WiFi> wifilistFromDevice, ArrayList<WiFi> wifiListFromDataBase){
        ArrayList<WiFi> tempListFromDevice=new ArrayList<>();
        tempListFromDevice.addAll(wifilistFromDevice);
        ArrayList<WiFi> choosedWifis = new ArrayList<>();

        choosedWifis.clear();

        for(WiFi databaseList : wifiListFromDataBase){
            for(WiFi deviceList : tempListFromDevice){
                if(databaseList.getName().equals(deviceList.getName())){
                    choosedWifis.add(calculateDistance(databaseList,deviceList));
                }
            }
        }
        tempListFromDevice.clear();
        Collections.sort(choosedWifis, new Comparator<WiFi>() {
            @Override
            public int compare(WiFi o1, WiFi o2) {
                if(o1.getPercentage()>o2.getPercentage()){
                    return -1;
                }
                return 1;
            }
        });
        Log.d(TAG,"Sorted in chooseWifis: " + choosedWifis.toString());
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
        return wifiListFromDevice;
    }

    public ArrayList<UserOnCanvas> getOnlineUsers() {
        return onlineUsers;
    }


    @Override
    public void run() {
        Communication.getInstance().setSendDataToUIListener(sendDataToUIListener);
        Communication.getInstance().setSendMessageFromReaderThreadToManager(sendMessageFromReaderThreadToManager);
        inintWifiScanReceiver();
        startRefresh();

    }

    private void startDrawPositionFragment(){
        DrawPositionFragmentI drawPositionFragment = new DrawPositionFragmentI(WiFiManagerSuperClass.getContext());
        drawPositionFragment.setISendDataToUIListener(sendDataToUIListener);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, drawPositionFragment);
        fragmentTransaction.addToBackStack("searchwifi");
        fragmentTransaction.commit();
    }

    public void setISendDataToUIListener(ISendDataToUIListener ISendDataToUIListener){
        this.sendDataToUIListener = ISendDataToUIListener;
    }

    @Override
    public void returnMessageFromReaderThread(String message) {

        String[] parts = message.split("-");
        Log.d(TAG, "returnMessageFromReaderThread: message:" + message);
        try {
            if(parts[0].equals("[ConnectionOK]")){
                sendDataToUIListener.returnMessage("Waiting for data...");
                startDrawPositionFragment();
            }
            else if(parts[0].equals("[Wifis]")){
                setWifisFromDatabase(parts[1]);
            }
            else if(parts[0].equals("[ReferencePoints]")){
                if(parts[1].equals("non~")){
                    sendDataToUIListener.returnMessage("No reference point in selected floor.");
                }
                else {
                    setReferencePointsFromDatabase(parts[1]);
                }
            }
            else if(parts[0].equals("[ReferencePointWifis]")){
                setReferencePointWifisFromDatabase(parts[1]);

            }
            else if(parts[0].equals("[ReferenceWifiPointsFromDatabaseEnd]")){
                sendMessageFromManagerToWiFiReferencePointsFragment.drawReferncePoints(true);
            }
            else if(parts[0].equals("[Exit]")){
                Communication.getInstance().destroy();
            }
            else if(parts[0].equals("[Users]")){
                setUsers(parts[1]);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            sendDataToUIListener.returnMessage("Hiba: " + e.toString());
        }
    }

    private void setUsers(String message){
        String[] parts = message.split("'");
        onlineUsers.clear();
        for(int i=0;i<parts.length;i++){
            String[] data = parts[i].split("~");
            if(data.length == 3){
                onlineUsers.add(new UserOnCanvas(data[0],Float.parseFloat(data[1])+random.nextInt(50)-25,Float.parseFloat(data[2])+random.nextInt(50)-25));
            }
        }
    }

    private void setReferencePointsFromDatabase(String message){
       String[] parts = message.split("~");
       referencePointsFromDatabase.clear();
       for(int i=0;i<parts.length;i++){
           referencePointsFromDatabase.add(new ReferencePoint(Integer.parseInt(parts[i])));
       }
    }

    private void setReferencePointWifisFromDatabase(String message){
        String[] parts = message.split("'");
        for(int i=0;i<parts.length;i++){
            String[] data = parts[i].split("~");
            if(data.length==7){
                for(ReferencePoint it : referencePointsFromDatabase){
                    if(it.getId() == Integer.parseInt(data[0])){
                        it.addWifi(new WiFiReference(Integer.parseInt(data[0]),
                                Integer.parseInt(data[1]),
                                Integer.parseInt(data[2]),
                                Integer.parseInt(data[3]),
                                data[4],
                                Double.parseDouble(data[5]),
                                Double.parseDouble(data[6])));
                    }
                }
            }
        }
        sendMessageFromManagerToMainActivity.messageFromManagerToMainActivity(referencePointsFromDatabase);
    }

    private void setWifisFromDatabase(String message){
        makeWifis(message);
        if (wifiListFromDataBase.size() < 3) {
            sendDataToUIListener.returnMessage("Not enough wifi.");
        }
    }

    private void makeWifis(String input){
        String[] parts = input.split("'");
        wifiListFromDataBase.clear();
        for(int i=0;i<parts.length;i++){
            String[] data = parts[i].split("~");
            if(data.length == 4){
                try{
                    wifiListFromDataBase.add(new WiFi(Integer.valueOf(data[0]),data[1],Integer.valueOf(data[2]),Integer.valueOf(data[3])));
                }
                catch (Exception ignored){}
            }
        }
    }

    public void setISendMessageFromManagerToMainActivity(ISendMessageFromManagerToMainActivity sendMessageFromManagerToMainActivity){
        this.sendMessageFromManagerToMainActivity = sendMessageFromManagerToMainActivity;
    }

    public void setISendMessageFromManagerToWiFiReferencePointsFragment(ISendMessageFromManagerToWiFiReferencePointsFragment received){
        this.sendMessageFromManagerToWiFiReferencePointsFragment = received;
    }
}