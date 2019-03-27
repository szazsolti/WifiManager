package ro.ms.sapientia.zsolti.wifimanager.Communication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.GetMessageInFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.GetMessageListener;

public class ReaderThread implements Runnable{

    private GetMessageListener getMessageListener;
    private GetMessageInFragment getMessageInFragment;
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
            socket = Client.getInstance().getClientSocket();
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            logged=true;
            while(logged){
                mess="";
                //socket=ss.accept();
                //Log.d(TAG,socket.getInetAddress().toString());
                if(socket.isConnected() && socket.isBound()){
                    Log.d(TAG,"In if, socket is connected: "+socket.isConnected()+ " and bound: "+socket.isBound());
                    try{
                        mess = bufferedReader.readLine();
                        //Log.d(TAG,"Message: "+ mess);
                    }
                    catch (Exception e){
                        inputStreamReader.close();
                        bufferedReader.close();
                        //socket.close();
                        getMessageListener.returnMessage("Socket is closed.");
                        logged=false;
                    }
                    //Log.d(TAG,"Message: "+mess);
                    if(mess!=null){
                        processingPachet(mess);
                        getMessageListener.returnMessage(mess);
                    }
                }
                else if(socket == null || socket.isClosed() || !socket.isConnected() || !socket.isBound() || mess==null){
                    inputStreamReader.close();
                    bufferedReader.close();
                    socket.close();
                    getMessageListener.returnMessage("Socket is closed.");
                    logged=false;
                }
                else{
                    inputStreamReader.close();
                    bufferedReader.close();
                    socket.close();
                    getMessageListener.returnMessage("Socket is closed.");
                    logged=false;
                }
            }
        }catch (Exception e){
            getMessageListener.returnMessage("Socket is closed.");
                //e1.printStackTrace();
            }
    }

    public void processingPachet(String input){
        String[] parts = input.split("-");
        try {
            if(parts[0].equals("[ConnectionOK]")){
                getMessageInFragment.returnMessage("OK");
            }
            else if(parts[0].equals("[Wifis]")){
                getMessageInFragment.returnMessage(input);
            }
            else if(parts[0].equals("[Exit]")){
                inputStreamReader.close();
                bufferedReader.close();
                socket.close();
                logged=false;
                getMessageInFragment.returnMessage("Exit");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setGetMessageInFragment(GetMessageInFragment getMessageInFragment){
        this.getMessageInFragment = getMessageInFragment;
    }

    public void setGetMessageListener(GetMessageListener getMessageListener){
        this.getMessageListener = getMessageListener;
    }

}
