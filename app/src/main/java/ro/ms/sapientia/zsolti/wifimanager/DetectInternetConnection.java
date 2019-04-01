package ro.ms.sapientia.zsolti.wifimanager;

import android.content.Context;
import android.net.ConnectivityManager;

public class DetectInternetConnection {
    private Context context;

    public DetectInternetConnection(Context context){
        this.context = context;
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();

    }
}
