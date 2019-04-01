package ro.ms.sapientia.zsolti.wifimanager.Communication;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private static Client sinlge_instance = null;
    private String username="User";

    private Client() throws IOException {
    }

    public static Client getInstance() throws IOException {
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


}