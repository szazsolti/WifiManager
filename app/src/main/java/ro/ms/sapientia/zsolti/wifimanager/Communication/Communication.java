package ro.ms.sapientia.zsolti.wifimanager.Communication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromReaderThreadToManager;

public class Communication extends AsyncTask<Void, Void, Void> {
    private Socket clientSocket = null;
    private int tryCount = 3;
    private static Communication sinlge_instance = null;
    private Thread threadRead = new Thread();
    private ReaderThread readerThread;

    private String TAG = "Communication";

    private ISendDataToUIListener sendDataToUIListener;
    private ISendMessageFromReaderThreadToManager sendMessageFromReaderThreadToManager;

    private Communication(){
    }

    @Override
    protected Void doInBackground(Void... voids) {


        for(int i = 0;i<tryCount;i++){
            try {
                clientSocket = new Socket("192.168.173.1",6554);
                Thread.sleep(500);
                break;
            } catch (IOException e) {
                sendDataToUIListener.returnMessage("Server is not available.");
                Log.d(TAG, "doInBackground: socket initialization failed");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(clientSocket==null){
            return null;
        }
        else {
            readerThread = new ReaderThread();
            readerThread.setISendDataToUIListener(sendDataToUIListener);
            readerThread.setISendMessageFromReaderThreadToManager(sendMessageFromReaderThreadToManager);
            readerThread.setSocket(clientSocket);
            this.threadRead = new Thread(readerThread);
            threadRead.start();
        }

        return null;
    }

    public static Communication getInstance(){
        if (sinlge_instance == null) {
            synchronized (Client.class) {
                if (sinlge_instance == null) {
                    sinlge_instance = new Communication();
                }
            }
        }
        return sinlge_instance;
    }

    public Socket getClientSocket(){
        return clientSocket;
    }

    public void sendMessage(final String message){
        MessageSender messageSender = new MessageSender();
        messageSender.setISendDataToUIListener(sendDataToUIListener);
        messageSender.execute(message);
    }

    public void setSendDataToUIListener(ISendDataToUIListener sendDataToUIListener){
        this.sendDataToUIListener=sendDataToUIListener;
    }

    public void setSendMessageFromReaderThreadToManager(ISendMessageFromReaderThreadToManager sendMessageFromReaderThreadToManager){
        this.sendMessageFromReaderThreadToManager=sendMessageFromReaderThreadToManager;
    }

    public void sendUsername(){
        sendMessage("[Username]-"+Client.getInstance().getUsername());
    }

    public void destroy(){
        try{
            Log.d(TAG, "destroy: ");
            sendMessage("[Logout]-");
            clientSocket.close();
            clientSocket=null;
            readerThread.myStop();
            cancel(true);
            sinlge_instance=null;
        }
        catch (Exception e){
        }
    }

}
