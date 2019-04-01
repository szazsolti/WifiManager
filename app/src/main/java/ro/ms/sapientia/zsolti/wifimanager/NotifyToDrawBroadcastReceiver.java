package ro.ms.sapientia.zsolti.wifimanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.NotifyToDraw;

public class NotifyToDrawBroadcastReceiver extends BroadcastReceiver {
    private String TAG = "NOTIFYTODRAWBROADCASTRECEIVER";
    static NotifyToDraw notifyToDraw = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String receivedText = intent.getStringExtra("notifyToDraw");
        Log.d(TAG,"Broadcast receiver received: " + receivedText + " context->" + context.toString() + " ntfdrv->" + notifyToDraw.toString() + "this->"+ this.toString());
        if (notifyToDraw != null) {
            notifyToDraw.notifyToDraw("draw");
        }
    }

    public void setNotifyToDraw (NotifyToDraw notifyToDraw){
        Log.d(TAG, "setNotifyToDraw: " + this.toString());
        NotifyToDrawBroadcastReceiver.notifyToDraw = notifyToDraw;
        //this.notifyToDraw = notifyToDraw;
    }
}
