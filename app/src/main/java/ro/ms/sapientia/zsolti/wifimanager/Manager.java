
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
import ro.ms.sapientia.zsolti.wifimanager.Fragments.DrawPositionFragmentI;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromManagerToMainActivity;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromReaderThreadToManager;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromManagerToWiFiReferencePointsFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromWifiScanReceiverToManager;

public class Manager implements ISendWiFiListFromWifiScanReceiverToManager, Runnable, ISendMessageFromReaderThreadToManager {

    private static Manager sinlge_instance = null;
    private String TAG = "MANAGER";
    private ArrayList<WiFi> wifiListFromDataBase = new ArrayList<>();
    private ArrayList<WiFi> wifiListFromDevice = new ArrayList<>();
    private ArrayList<ReferencePoint> referencePointsFromDatabase = new ArrayList<>();
    private WifiManager mainWifiObj;
    private WifiScanReceiver wifiReciever;
    private Thread refreshWifi = new Thread();
    private Thread startConnection;
    private ISendMessageFromManagerToMainActivity sendMessageFromManagerToMainActivity;
    //private ISendWiFiListFromManagerToWiFiReferencePointsFragment sendWiFiListFromManagerToWiFiReferencePointsFragment;

    //private INotifyToDraw INotifyToDraw;
    //private Thread refreshData;
    //private Context context;
    private FragmentManager fragmentManager;
    private ISendDataToUIListener sendDataToUIListener;
    private ISendMessageFromReaderThreadToManager sendMessageFromReaderThreadToManager = Manager.this;

    private Manager(){
        //this.context = context;
        //if(checkPermission()) {
        //inintWifiScanReceiver();

        //}
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

/*
    public static boolean init(Context context) {
        if (sinlge_instance == null) {
            synchronized (Manager.class) {
                if (sinlge_instance == null) {
                    sinlge_instance = new Manager(context.getApplicationContext());
                    return true;
                }
            }
        }
        return false;
    }
*/

    public void stopManager(){
        //startConnection.destroy();
        //mainWifiObj.;
        //wifiReciever.abortBroadcast();
        //Looper.getMainLooper().quit();

        try{
            WiFiManagerSuperClass.getContext().unregisterReceiver(wifiReciever);
        }catch (Exception e){}

        refreshWifi=null;
        startConnection=null;
        //wifiReciever=null;
        /*
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future longRunningTaskFuture = executorService.submit(this);
        longRunningTaskFuture.cancel(true);*/
    }


    public boolean startCommunication() {
        inintWifiScanReceiver();
        startConnection=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Communication.getInstance().setSendDataToUIListener(sendDataToUIListener);

                    //Communication.getInstance().setSendMessageFromReaderThreadToHomeFragment(sendMessageFromReaderThreadToHomeFragment);
                    Communication.getInstance().setSendMessageFromReaderThreadToManager(sendMessageFromReaderThreadToManager);
                    if(!Communication.getInstance().readerThreadIsRunning()){
                        Communication.getInstance().initParams();
                        Communication.getInstance().startReaderThread();
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    sendDataToUIListener.returnMessage("Failed to connect to server.");
                }
            }
        });
        if(!startConnection.isAlive()){
            startConnection.start();
        }

        return startConnection.isAlive();
    }



    @Override
    public void returnWiFiListFromDevice(ArrayList<WiFi> wifilistFromDevice) {
        //Log.d(TAG, "Wifis returned: " + stringArray[0]);

        Log.d(TAG, "returnWiFiListFromDevice: "+wifilistFromDevice.toString());
        this.wifiListFromDevice=new ArrayList<>();
        this.wifiListFromDevice=wifilistFromDevice;

        //ertesiteni a wifireferencepointsfragment-et
        //sendWiFiListFromManagerToWiFiReferencePointsFragment.returnWiFiListFromDevice(wifilistFromDevice);

        //Log.d(TAG, "returnWiFiListFromDeviceInManager: " + wifiListFromDevice.toString());
        try{
            calculateTrilateration(wifilistFromDevice);
        }
        catch (Exception e){
            //e.printStackTrace();
        }
    }


    public void setWifiListFromDataBase(ArrayList<WiFi> wifis){
        this.wifiListFromDataBase = wifis;
    }

    public ArrayList<WiFi> getWifiListFromDataBase() {
        return wifiListFromDataBase;
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
        return wifiListFromDevice;
    }
/*
    public void setContext(Context context) {
        this.context = context;
    }
*/

    public ArrayList<ReferencePoint> getReferencePointsFromDatabase(){
        Log.d(TAG, "getReferencePointsFromDatabase: " + referencePointsFromDatabase.size());
        return referencePointsFromDatabase;
    }

    @Override
    public void run() {
        /*
        try {
            startCommunication();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }

    private void startDrawPositionFragment(){
        DrawPositionFragmentI drawPositionFragment = new DrawPositionFragmentI(WiFiManagerSuperClass.getContext());
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

    @Override
    public void returnMessageFromReaderThread(String message) {

        String[] parts = message.split("-");
        //Log.d(TAG, "returnMessageFromReaderThread: message:" + message);
        try {
            if(parts[0].equals("[ConnectionOK]")){
                sendDataToUIListener.returnMessage("Waiting for data...");
                //Log.d(TAG, "returnMessageFromReaderThread: ConnectionOK is received");
                startDrawPositionFragment();
            }
            else if(parts[0].equals("[Wifis]")){
                //sendMessageFromReaderThreadToHomeFragment.returnMessage(message);
                //Log.d(TAG, "returnMessageFromReaderThread: parts[1]" + parts[1]);
                setWifisFromDatabase(parts[1]);

            }
            else if(parts[0].equals("[ReferencePoints]")){
                //Log.d(TAG, "returnMessageFromReaderThread: " + parts[1]);
                setReferencePointsFromDatabase(parts[1]);
            }
            else if(parts[0].equals("[ReferencePointWifis]")){
                setReferencePointWifisFromDatabase(parts[1]);
            }
            else if(parts[0].equals("[ReferenceWifiPointsFromDatabaseEnd]")){
                sendDataToUIListener.returnMessage("All data is received.");
            }
            else if(parts[0].equals("[Exit]")){
                /*
                inputStreamReader.close();
                bufferedReader.close();
                socket.close();
                logged=false;*/
                Communication.getInstance().destroy();

                //sendMessageFromReaderThreadToHomeFragment.returnMessage("Exit");
            }
        }
        catch (Exception e){
            //e.printStackTrace();
            sendDataToUIListener.returnMessage("Hiba: " + e.toString());
        }

    }

    private void setReferencePointsFromDatabase(String message){
       String[] parts = message.split("~");
        for(int i=0;i<parts.length;i++){
            referencePointsFromDatabase.add(new ReferencePoint(Integer.parseInt(parts[i])));
        }/*
        for (ReferencePoint it : referencePointsFromDatabase){
            Log.d(TAG, "setReferencePointsFromDatabase: " + it.getId());
        }*/
    }

    private void setReferencePointWifisFromDatabase(String message){
        String[] parts = message.split("'");
        //Log.d(TAG, "setReferencePointWifisFromDatabase: " + message);
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
        if(wifiListFromDataBase.size()>=3){
            Log.d(TAG,"WiFilista"+wifiListFromDataBase.toString());
        }
        else{
            sendDataToUIListener.returnMessage("Not enough wifi.");
        }
    }

    public void makeWifis(String input){
        String[] parts = input.split("~");
        wifiListFromDataBase.clear();
        for(int i=0;i<parts.length;i++){
            String[] data = parts[i].split(" ");
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
/*
    public void setISendWiFiListFromManagerToWiFiReferencePointsFragment(ISendWiFiListFromManagerToWiFiReferencePointsFragment sendWiFiListFromManagerToWiFiReferencePointsFragment){
        this.sendWiFiListFromManagerToWiFiReferencePointsFragment = sendWiFiListFromManagerToWiFiReferencePointsFragment;
    }*/
}