package ro.ms.sapientia.zsolti.wifimanager;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromManagerToWiFiReferencePointsFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromWifiScanReceiverToManager;

public class WifiScanReceiver extends BroadcastReceiver {
    private WifiManager mainWifiObj;
    private ArrayList<String> wifis;
    private ArrayList<WiFi> wifisFromDevice=new ArrayList<>();
    private ISendWiFiListFromWifiScanReceiverToManager sendWiFiListFromDeviceArrayListFromWifiScanReceiverToManager;

    private String TAG = "WIFISCANRECEIVER";

    public WifiScanReceiver(WifiManager wifiManager){
        this.mainWifiObj = wifiManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent != null && intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        {
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
            wifis = new ArrayList<>();
            for(int i = 0; i < wifiScanList.size(); i++){
                wifis.add((wifiScanList.get(i)).toString());
            }
            wifisFromDevice.clear();

            for (String wifi : wifis){
                String[] temp = wifi.split(",");
                String ssid = temp[0].substring(5).trim();
                String macAddress =  temp[1].substring(8);
                double frequency = Double.parseDouble(temp[4].substring(11));
                double level = Double.parseDouble(temp[3].substring(7).trim());
                WiFi tempWifi = new WiFi(macAddress,level,frequency);
                wifisFromDevice.add(tempWifi);
            }
            sendWiFiListFromDeviceArrayListFromWifiScanReceiverToManager.returnWiFiListFromDevice(wifisFromDevice);
        }
    }
    public void setISendWiFiListFromDeviceArrayListFromWifiScanReceiverToManager(ISendWiFiListFromWifiScanReceiverToManager ISendWiFiListFromWifiScanReceiverToManager){
        this.sendWiFiListFromDeviceArrayListFromWifiScanReceiverToManager = ISendWiFiListFromWifiScanReceiverToManager;
    }

}
