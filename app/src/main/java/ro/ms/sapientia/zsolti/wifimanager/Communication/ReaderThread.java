package ro.ms.sapientia.zsolti.wifimanager.Communication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Calendar;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromReaderThreadToManager;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.WiFiManagerSuperClass;

public class ReaderThread implements Runnable{

    private ISendDataToUIListener sendDataToUIListener;
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
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            logged=true;
            while(logged){
                mess="";
                if(socket.isConnected() && socket.isBound()){
                    try{
                        mess = bufferedReader.readLine();
                    }
                    catch (Exception e){
                        inputStreamReader.close();
                        bufferedReader.close();
                        socket.close();
                        sendDataToUIListener.returnMessage("Socket is closed1.");
                        logged=false;
                    }
                    if(mess!=null){
                        processingPachet(mess);
                    }
                }
                else if(socket == null || socket.isClosed() || !socket.isConnected() || !socket.isBound() || mess==null){
                    inputStreamReader.close();
                    bufferedReader.close();
                    socket.close();
                    sendDataToUIListener.returnMessage("Socket is closed2.");
                    logged=false;
                }
                else{
                    inputStreamReader.close();
                    bufferedReader.close();
                    socket.close();
                    sendDataToUIListener.returnMessage("Socket is closed3.");
                    logged=false;
                }
            }
        }catch (Exception e){
            }
    }

    public void myStop(){
        logged=false;
        try {
            socket.close();
        } catch (IOException e) {
        }
        socket=null;
    }

    public void setSocket(Socket socket){
        this.socket=socket;
    }


    public void processingPachet(String input){

        Log.d(TAG, "processingPachet: input: " + input);
        sendMessageFromReaderThreadToManager.returnMessageFromReaderThread(input);
    }


    public void setISendMessageFromReaderThreadToManager(ISendMessageFromReaderThreadToManager ISendMessageFromReaderThreadToManager){
        this.sendMessageFromReaderThreadToManager = ISendMessageFromReaderThreadToManager;
    }

    public void setISendDataToUIListener(ISendDataToUIListener ISendDataToUIListener){
        this.sendDataToUIListener = ISendDataToUIListener;
    }

}
