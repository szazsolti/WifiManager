package ro.ms.sapientia.zsolti.wifimanager.Communication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.GetMessageListener;

public class MessageSender extends AsyncTask<String,Void,Void> {
    private Socket clientSocket;
    private DataOutputStream dataOutputStream;
    private GetMessageListener getMessageListener;
    private String TAG = "MESSAGESENDER_CLASS";
    String message;

    //public MessageSender(Socket socket){
    //     this.clientSocket = socket;
    // }

    @Override
    protected Void doInBackground(String... voids) {
        PrintWriter pw=null;
        String message = voids[0];

        try {
            clientSocket = Client.getInstance().getClientSocket();
            //Client.getInstance().setSocket(clientSocket);

            if(clientSocket.isConnected() && clientSocket.isBound()){
                //getMessageListener.returnMessage("The socket is OK.");
                pw = new PrintWriter(clientSocket.getOutputStream());
                Log.d(TAG,"Kuldott uzenet: "+message);

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
                    //pw.close();
                    //clientSocket.close();
                }
                else{
                    pw.flush();
                    pw.close();
                    clientSocket.close();
                    getMessageListener.returnMessage("The socket is closed. Server is not available.");
                }

            }
            else{
                //assert pw != null;
                pw.flush();
                pw.close();
                clientSocket.close();
                getMessageListener.returnMessage("The socket is closed. Server is not available.");
            }



        } catch (IOException e) {
            getMessageListener.returnMessage("The socket is closed. Server is not available.");
            //e.printStackTrace();
        }

        return null;
    }

    //public void setSocket(Socket socket){
    //    this.clientSocket = socket;
    // }


    public void setGetMessageListener(GetMessageListener getMessageListener){
        this.getMessageListener = getMessageListener;
    }
}

