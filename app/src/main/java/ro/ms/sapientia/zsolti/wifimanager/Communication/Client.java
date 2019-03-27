package ro.ms.sapientia.zsolti.wifimanager.Communication;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private static Client sinlge_instance = null;
    private Socket clientSocket = new Socket("192.168.173.1",6554);

    private Client() throws IOException {
    }

    public static Client getInstance() throws IOException {
        if(sinlge_instance==null){
            sinlge_instance = new Client();
        }
        return sinlge_instance;
    }

    public void setSocket(Socket socket){
        this.clientSocket = socket;
    }

    public Socket getClientSocket(){
        return clientSocket;
    }

    public void destroy(){
        try{
            clientSocket.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}