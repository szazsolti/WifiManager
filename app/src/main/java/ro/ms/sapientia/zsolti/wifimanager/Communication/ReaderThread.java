package ro.ms.sapientia.zsolti.wifimanager.Communication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Calendar;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromReaderThreadToManager;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.WiFiManagerSuperClass;

public class ReaderThread implements Runnable{

    private ISendDataToUIListener sendDataToUIListener;
    //private ISendMessageFromReaderThreadToHomeFragment sendMessageFromReaderThreadToHomeFragment;
    private ISendMessageFromReaderThreadToManager sendMessageFromReaderThreadToManager;
    private String TAG = "READERTHREAD_CLASS";
    private Socket socket=null;
    private InputStreamReader inputStreamReader=null;
    private BufferedReader bufferedReader=null;
    private Boolean logged=false;

    @Override
    public void run() {
        String mess;
        logged=false;
        try{
            //socket = Client.getInstance().getClientSocket();
            //SocketService service = new SocketService();
            //socket = Communication.getInstance().getClientSocket();
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            logged=true;
            while(logged){
                mess="";
                //socket=ss.accept();
                //Log.d(TAG,socket.getInetAddress().toString());
                if(socket.isConnected() && socket.isBound()){
                    //Log.d(TAG,"In if, socket is connected: "+socket.isConnected()+ " and bound: "+socket.isBound());
                    try{
                        mess = bufferedReader.readLine();
                        //Log.d(TAG,"Message: "+ mess);
                    }
                    catch (Exception e){
                        inputStreamReader.close();
                        bufferedReader.close();
                        socket.close();
                        sendDataToUIListener.returnMessage("Socket is closed1.");

                        //Client.getInstance().destroy();
                        logged=false;
                        killApp();
                    }
                    //Log.d(TAG,"Message: "+mess);
                    if(mess!=null){
                        processingPachet(mess);
                        //sendDataToUIListener.returnMessage(mess);
                    }
                }
                else if(socket == null || socket.isClosed() || !socket.isConnected() || !socket.isBound() || mess==null){
                    inputStreamReader.close();
                    bufferedReader.close();
                    socket.close();
                    sendDataToUIListener.returnMessage("Socket is closed2.");
                    logged=false;
                    killApp();
                }
                else{
                    inputStreamReader.close();
                    bufferedReader.close();
                    socket.close();
                    sendDataToUIListener.returnMessage("Socket is closed3.");
                    logged=false;
                    killApp();
                }
            }
        }catch (Exception e){
            //sendDataToUIListener.returnMessage("Socket is closed4.");
                //e1.printStackTrace();
            killApp();
            }
    }

    private void killApp(){
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }


    public void setSocket(Socket socket){
        this.socket=socket;
    }


    public void processingPachet(String input){

        //Log.d(TAG, "processingPachet: input: " + input);
        sendMessageFromReaderThreadToManager.returnMessageFromReaderThread(input);

        /*
        String[] parts = input.split("-");
        try {
            if(parts[0].equals("[ConnectionOK]")){
                sendDataToUIListener.returnMessage("Waiting for data...");
            }
            else if(parts[0].equals("[Wifis]")){
                sendMessageFromReaderThreadToHomeFragment.returnMessage(input);
            }
            else if(parts[0].equals("[ReferencePoints]")){

            }
            else if(parts[0].equals("[Exit]")){
                inputStreamReader.close();
                bufferedReader.close();
                socket.close();
                logged=false;
                sendMessageFromReaderThreadToHomeFragment.returnMessage("Exit");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }*/
    }
/*
    public void setISendMessageFromReaderThreadToHomeFragment(ISendMessageFromReaderThreadToHomeFragment ISendMessageFromReaderThreadToHomeFragment){
        this.sendMessageFromReaderThreadToHomeFragment = ISendMessageFromReaderThreadToHomeFragment;
    }*/

    public void setISendMessageFromReaderThreadToManager(ISendMessageFromReaderThreadToManager ISendMessageFromReaderThreadToManager){
        this.sendMessageFromReaderThreadToManager = ISendMessageFromReaderThreadToManager;
    }

    public void setISendDataToUIListener(ISendDataToUIListener ISendDataToUIListener){
        this.sendDataToUIListener = ISendDataToUIListener;
    }

}
