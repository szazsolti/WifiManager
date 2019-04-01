package ro.ms.sapientia.zsolti.wifimanager.Communication;

import java.io.IOException;
import java.net.Socket;

import ro.ms.sapientia.zsolti.wifimanager.Fragments.HomeFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromReaderThreadToHomeFragment;

public class Communication {
    private Socket clientSocket = new Socket("192.168.173.1",6554);

    private static Communication sinlge_instance = null;
    private Thread readerThread = new Thread();

    private ISendDataToUIListener sendDataToUIListener;
    private ISendMessageFromReaderThreadToHomeFragment sendMessageFromReaderThreadToHomeFragment;

    private Communication() throws IOException {
    }

    public void initParams(){
        ReaderThread readerThread = new ReaderThread();
        readerThread.setISendDataToUIListener(sendDataToUIListener);
        readerThread.setISendMessageFromReaderThreadToHomeFragment(sendMessageFromReaderThreadToHomeFragment);
        readerThread.setSocket(clientSocket);
        Communication.this.readerThread = new Thread(readerThread);
    }

    public static Communication getInstance() throws IOException {
        if (sinlge_instance == null) {
            synchronized (Client.class) {
                if (sinlge_instance == null) {
                    sinlge_instance = new Communication();
                }
            }
        }
        return sinlge_instance;
    }

    public boolean isExists(){
        return sinlge_instance != null;
    }

    public Socket getClientSocket(){
        return clientSocket;
    }

    public boolean connected(){
        return clientSocket.isConnected();
    }

    public void startReaderThread(){
        Communication.this.readerThread.start();
    }

    public void stopReaderThread(){
        Communication.this.readerThread.stop();
    }

    public boolean readerThreadIsRunning(){
        return readerThread.isAlive();
    }

    public void sendMessage(String message){
        MessageSender messageSender = new MessageSender();
        messageSender.execute(message);
    }

    public void setSendDataToUIListener(ISendDataToUIListener sendDataToUIListener){
        this.sendDataToUIListener=sendDataToUIListener;
    }

    public void setSendMessageFromReaderThreadToHomeFragment(ISendMessageFromReaderThreadToHomeFragment sendMessageFromReaderThreadToHomeFragment){
        this.sendMessageFromReaderThreadToHomeFragment=sendMessageFromReaderThreadToHomeFragment;
    }

    public void sendUsername() throws IOException {
        sendMessage("[Username-]"+Client.getInstance().getUsername());
    }

    public void destroy(){
        try{
            clientSocket.close();
            clientSocket=null;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
