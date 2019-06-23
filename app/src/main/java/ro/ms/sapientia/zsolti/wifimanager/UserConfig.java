package ro.ms.sapientia.zsolti.wifimanager;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;

public class UserConfig {
    private SharedPreferences preference;
    SharedPreferences.Editor editor;

    public UserConfig() {
        try {
            preference = getApplicationUsingReflection().getApplicationContext().getSharedPreferences("WiFiManager", 0);
            editor = preference.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void writeStringData(String value, String key){
        editor.putString(key,value);
        editor.commit();
    }

    public void writeIntData(int value, String key){
        editor.putInt(key,value);
        editor.commit();
    }

    public String readStringData(String key) {
        return preference.getString(key, "");
    }

    public int readIntData(String key) {
        return preference.getInt(key, Color.RED);
    }

    public boolean isExistStringData(String key){
        return !preference.getString(key, "").equals("");
    }

    public boolean isExistIntData(String key){
        return preference.getInt(key, -1)!=-1;
    }

    public boolean readBooleanData(String key) {
        return preference.getBoolean(key, false);
    }

    public void writeBooleanData(boolean value, String key){
        editor.putBoolean(key,value);
        editor.commit();
    }

    private static Application getApplicationUsingReflection() throws Exception {
        return (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null, (Object[]) null);
    }

}
