package ro.ms.sapientia.zsolti.wifimanager.Interfaces;

import java.util.ArrayList;

import ro.ms.sapientia.zsolti.wifimanager.WiFi;

public interface ISendWiFiListFromManagerToWiFiReferencePointsFragment {
    void returnWiFiListFromDevice(ArrayList<WiFi> wifisFromDevice);
}
