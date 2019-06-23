package ro.ms.sapientia.zsolti.wifimanager.Communication;

import android.graphics.Color;

import java.io.IOException;
import java.net.Socket;

import ro.ms.sapientia.zsolti.wifimanager.UserConfig;

public class Client {
    private static Client sinlge_instance = null;
    //private String username="User";
    private String xTrilat="0";
    private String yTrilat="0";
    private String xRef="0";
    private String yRef="0";
    private UserConfig userConfig = new UserConfig();


    private Client(){
    }

    public static Client getInstance(){
        if (sinlge_instance == null) {
            synchronized (Client.class) {
                if (sinlge_instance == null) {
                    sinlge_instance = new Client();
                }
            }
        }
        return sinlge_instance;
    }
/*
    public void setSocket(Socket socket){
        this.clientSocket = socket;
    }*/

    public void setUsername(String username){
        userConfig.writeStringData(username,"username");
    }

    public String getUsername(){
        if(userConfig.isExistStringData("username")){
            return userConfig.readStringData("username");
        }
        return "";
    }

    public void setClientDotColor(int color){
        userConfig.writeIntData(color,"clientDotColor");
    }

    public int getClientDotColor(){
        if(userConfig.isExistIntData("clientDotColor")){
            return userConfig.readIntData("clientDotColor");
        }
        return Color.RED;
    }

    public void setReferencePointDotColor(int color){
        userConfig.writeIntData(color,"referencePointDotColor");
    }

    public int getReferencePointDotColor(){
        if(userConfig.isExistIntData("referencePointDotColor")){
            return userConfig.readIntData("referencePointDotColor");
        }
        return Color.GREEN;
    }

    public void setOnlineUsersDotColor(int color){
        userConfig.writeIntData(color,"onlineUsersDotColor");
    }

    public int getOnlineUsersDotColor(){
        if(userConfig.isExistIntData("onlineUsersDotColor")){
            return userConfig.readIntData("onlineUsersDotColor");
        }
        return Color.BLUE;
    }

    public String getXRef() {
        return xRef;
    }

    public void setXRef(String xRef) {
        this.xRef = xRef;
    }

    public String getYRef() {
        return yRef;
    }

    public void setYRef(String yRef) {
        this.yRef = yRef;
    }

    public String getXTrilat() {
        return xTrilat;
    }

    public void setXTrilat(String x) {
        this.xTrilat = x;
    }

    public String getYTrilat() {
        return yTrilat;
    }

    public void setYTrilat(String y) {
        this.yTrilat = y;
    }
}