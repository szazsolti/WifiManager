package ro.ms.sapientia.zsolti.wifimanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.INotifyToDraw;

public class NotifyToDrawBroadcastReceiver extends BroadcastReceiver {
    private String TAG = "NOTIFYTODRAWBROADCASTRECEIVER";
    static INotifyToDraw INotifyToDraw = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String receivedText = intent.getStringExtra("INotifyToDraw");
        Log.d(TAG,"Broadcast receiver received: " + receivedText + " context->" + context.toString() + " ntfdrv->" + INotifyToDraw.toString() + "this->"+ this.toString());
        if (INotifyToDraw != null) {
            INotifyToDraw.notifyToDraw("draw");
        }
    }

    public void setNotifyToDraw (INotifyToDraw INotifyToDraw){
        Log.d(TAG, "setNotifyToDraw: " + this.toString());
        NotifyToDrawBroadcastReceiver.INotifyToDraw = INotifyToDraw;
    }
}
