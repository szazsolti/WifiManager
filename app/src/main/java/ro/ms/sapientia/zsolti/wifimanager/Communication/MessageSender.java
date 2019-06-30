package ro.ms.sapientia.zsolti.wifimanager.Communication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;

public class MessageSender extends AsyncTask<String,Void,Void> {
    private Socket clientSocket;
    private DataOutputStream dataOutputStream;
    private ISendDataToUIListener ISendDataToUIListener;
    private String TAG = "MESSAGESENDER_CLASS";
    @Override
    protected Void doInBackground(String... voids) {
        PrintWriter pw=null;
        String message = voids[0];

        try {

            clientSocket = Communication.getInstance().getClientSocket();

            if(clientSocket != null && clientSocket.isConnected() && clientSocket.isBound()){
                pw = new PrintWriter(clientSocket.getOutputStream());

                if(message.contains("[Logout]-")){
                    pw.write(message.length()+"~"+message + '\n');
                    Log.d(TAG,"LOGOUT: ");
                    pw.flush();
                    pw.close();
                    clientSocket.close();
                }
                else if(!message.equals("null")){
                    pw.write(message.length()+"~"+message + '\n');
                    pw.flush();
                }
                else{
                    pw.flush();
                    pw.close();
                    clientSocket.close();
                    ISendDataToUIListener.returnMessage("The socket is closed. Server is not available.");
                }
            }
            else{
                try{
                    ISendDataToUIListener.returnMessage("The socket is closed. Server is not available.");
                }
                catch (Exception ignored){}
            }
            

        } catch (IOException e) {
            try {
                ISendDataToUIListener.returnMessage("Server is not available.");
            }
            catch (Exception ignored){
            }
        }

        return null;
    }


    public void setISendDataToUIListener(ISendDataToUIListener ISendDataToUIListener){
        this.ISendDataToUIListener = ISendDataToUIListener;
    }
}

