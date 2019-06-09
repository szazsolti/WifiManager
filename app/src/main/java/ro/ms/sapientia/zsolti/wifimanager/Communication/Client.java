package ro.ms.sapientia.zsolti.wifimanager.Communication;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private static Client sinlge_instance = null;
    private String username="User";
    private String xTrilat="0";
    private String yTrilat="0";
    private String xRef="0";
    private String yRef="0";

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
        this.username = username;
    }

    public String getUsername(){
        return this.username;
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