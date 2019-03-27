/*
package ro.ms.sapientia.zsolti.wifimanager;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.GetWiFiListFromDeviceArrayList;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.NotifyToDraw;
import ro.ms.sapientia.zsolti.wifimanager.Trilateration;

public class Manager {

    private static Manager single_instance = null;

    private Manager(){}
    private ArrayList<WiFi> wifiList = new ArrayList<>();
    //private NotifyToDraw notifyToDraw;
    private Thread refreshData;
    private Context context;

    public interface NotifyDraw{
        void notify(boolean value);
    }

    private NotifyDraw notifyDraw;

    public void setListener(NotifyDraw notifyDraw){
        this.notifyDraw=notifyDraw;
    }

    public static Manager getInstance(){
        if(single_instance == null){
            single_instance = new Manager();
        }
        return single_instance;
    }

    public void setDatabaseWifilist(ArrayList<WiFi> wifiList) {
        this.wifiList = wifiList;
    }

    public void getWifiData(){
        refreshData = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    WifiManager mainWifiObj = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    mainWifiObj.startScan();
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void returnWiFiListFromDevice(ArrayList<WiFi> wifisFromDevice) {
        try{
            //calculateTrilateration(wifisFromDevice);



            //notifyToDraw();

/*
            DrawPositionFragment drawPositionFragment = new DrawPositionFragment(context);
            //drawPositionFragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, drawPositionFragment);
            //fragmentTransaction.addToBackStack("homeFragment");
            fragmentTransaction.commit();*/
            //list.setAdapter(new ArrayAdapter<>(context,R.layout.list_item,R.id.label, stringArray));*/
/*
        }
        catch (Exception e){
            //e.printStackTrace();
        }
    }


}
*/