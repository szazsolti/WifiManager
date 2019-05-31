package ro.ms.sapientia.zsolti.wifimanager;

import android.app.Application;
import android.content.Context;

public class WiFiManagerSuperClass extends Application {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
