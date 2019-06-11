package ro.ms.sapientia.zsolti.wifimanager.Communication;

import java.io.IOException;
import java.net.Socket;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromReaderThreadToManager;

public class Communication {
    private Socket clientSocket = new Socket("192.168.173.1",6554);

    private static Communication sinlge_instance = null;
    private Thread threadRead = new Thread();
    private ReaderThread readerThread;

    private ISendDataToUIListener sendDataToUIListener;
    //private ISendMessageFromReaderThreadToHomeFragment sendMessageFromReaderThreadToHomeFragment;
    private ISendMessageFromReaderThreadToManager sendMessageFromReaderThreadToManager;

    private Communication() throws IOException {
    }

    public void initParams() throws IOException {
        if(clientSocket==null){
            clientSocket = new Socket("192.168.173.1",6554);
        }

        readerThread = new ReaderThread();
        readerThread.setISendDataToUIListener(sendDataToUIListener);
        readerThread.setISendMessageFromReaderThreadToManager(sendMessageFromReaderThreadToManager);
        //threadRead.setISendMessageFromReaderThreadToHomeFragment(sendMessageFromReaderThreadToHomeFragment);
        readerThread.setSocket(clientSocket);
        this.threadRead = new Thread(readerThread);
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
        if(clientSocket!=null){
            return clientSocket.isConnected();
        }
        else {
            return false;
        }
    }

    public void startReaderThread(){
        if(!this.threadRead.isAlive()){
            this.threadRead.start();
        }
    }

    public void stopReaderThread(){
        Communication.this.threadRead.stop();
    }

    public boolean readerThreadIsRunning(){
        return threadRead.isAlive();
    }

    public void sendMessage(final String message){
        MessageSender messageSender = new MessageSender();
        messageSender.setISendDataToUIListener(sendDataToUIListener);
        messageSender.execute(message);
    }

    public void setSendDataToUIListener(ISendDataToUIListener sendDataToUIListener){
        this.sendDataToUIListener=sendDataToUIListener;
    }
/*
    public void setSendMessageFromReaderThreadToHomeFragment(ISendMessageFromReaderThreadToHomeFragment sendMessageFromReaderThreadToHomeFragment){
        this.sendMessageFromReaderThreadToHomeFragment=sendMessageFromReaderThreadToHomeFragment;
    }
*/
    public void setSendMessageFromReaderThreadToManager(ISendMessageFromReaderThreadToManager sendMessageFromReaderThreadToManager){
        this.sendMessageFromReaderThreadToManager=sendMessageFromReaderThreadToManager;
    }

    public void sendUsername(){
        sendMessage("[Username]-"+Client.getInstance().getUsername());
    }

    public void destroy(){
        try{
            clientSocket.close();
            clientSocket=null;
            readerThread.myStop();
            //threadRead.stop();
        }
        catch (Exception e){
            //e.printStackTrace();
        }
    }

}
