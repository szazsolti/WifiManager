package ro.ms.sapientia.zsolti.wifimanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

public class SocketService extends Service {
    private Socket clientSocket=null;

    private Runnable socketThread = new Runnable() {
        @Override
        public void run() {
                try {
                    if(clientSocket == null)
                        clientSocket = new Socket("192.168.173.1",6554);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(socketThread).start();
        Toast.makeText(this,"The socket service is started.",Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Socket getClientSocket(){
        return clientSocket;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            clientSocket.close();
            Toast.makeText(this,"The socket service is stopped.",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
