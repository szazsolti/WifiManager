package ro.ms.sapientia.zsolti.wifimanager.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;
import ro.ms.sapientia.zsolti.wifimanager.Communication.Communication;
import ro.ms.sapientia.zsolti.wifimanager.Communication.MessageSender;
import ro.ms.sapientia.zsolti.wifimanager.Communication.ReaderThread;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromReaderThreadToHomeFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.WiFi;

public class HomeFragment extends Fragment implements ISendMessageFromReaderThreadToHomeFragment {

    private TextView tw_test;
    private Button bt_connect;
    private EditText et_username;
    private Thread readerThread = new Thread();
    private Thread managerThread;
    private Thread startConnection;
    private ISendDataToUIListener sendDataToUIListener;
    private ISendMessageFromReaderThreadToHomeFragment sendMessageFromReaderThreadToHomeFragment = HomeFragment.this;
    private String TAG="HOMEFRAGMENT";
    private Context context;
    private ArrayList<WiFi> wifiList = new ArrayList<>();
    String message="";

    @SuppressLint("ValidFragment")
    public HomeFragment(Context context) {
        // Required empty public constructor
        this.context=context;
    }

    public HomeFragment(){

    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        bt_connect = view.findViewById(R.id.bt_connect);
        tw_test = view.findViewById(R.id.tw_instruction);
        et_username = view.findViewById(R.id.et_username);
        startConnection=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Communication.getInstance();
                    Communication.getInstance().setSendDataToUIListener(sendDataToUIListener);
                    Communication.getInstance().setSendMessageFromReaderThreadToHomeFragment(sendMessageFromReaderThreadToHomeFragment);
                    if(!Communication.getInstance().readerThreadIsRunning()){
                        Communication.getInstance().initParams();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        startConnection.start();
/*
        if(!HomeFragment.this.readerThread.isAlive()) {
            ReaderThread readerThread = new ReaderThread();
            readerThread.setISendDataToUIListener(sendDataToUIListener);
            readerThread.setISendMessageFromReaderThreadToHomeFragment(this);
            HomeFragment.this.readerThread = new Thread(readerThread);
        }
*/
        //((MainActivity)Objects.requireNonNull(getActivity())).setISendMessageFromReaderThreadToHomeFragment((ISendMessageFromReaderThreadToHomeFragment) getContext());
        //context.startService(new Intent(context, SocketService.class));
        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //context.bindService(new Intent(context, SocketService.class));
                try {
                    if(!Communication.getInstance().readerThreadIsRunning()){
                        Communication.getInstance().startReaderThread();
                    }
                    sendMyUsername();
                } catch (Exception ignored) {
                    Log.d(TAG,"Exception, failed to send username.");
                    sendDataToUIListener.returnMessage("Failed to connect to server.");
                }/*
                if(!HomeFragment.this.readerThread.isAlive()){
                    HomeFragment.this.readerThread.start();
                }*/
                //((MainActivity)Objects.requireNonNull(getActivity())).setISendMessageFromReaderThreadToHomeFragment((ISendMessageFromReaderThreadToHomeFragment) getContext());
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

    }

    @Override
    public void returnMessage(String text) {
        processingMessage(text);
    }

    public void processingMessage(String message){
        String[] parts = message.split("-");

        if(parts[0].equals("[Wifis]")){
             makeWifis(parts[1]);
             if(wifiList.size()>=3){
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("wifilist", wifiList);
                Manager manager = new Manager(context);
                manager.setWifiListFromDataBase(wifiList);
                manager.setFragmentManager(getFragmentManager());
                manager.setISendDataToUIListener(sendDataToUIListener);
                managerThread = new Thread(manager);
                managerThread.start();
                Log.d(TAG,"WiFilista");
/*
                SearchWifiFragment searchWifiFragment = new SearchWifiFragment(context);
                searchWifiFragment.setArguments(bundle);
                //searchWifiFragment.setNotifyToDraw(notifyDraw);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, searchWifiFragment);
                //fragmentTransaction.addToBackStack("homeFragment");
                fragmentTransaction.commit();*/
             }
             else{
                 sendDataToUIListener.returnMessage("Not enough wifi.");
             }
        }
        else if(message.equals("Exit")){
            sendDataToUIListener.returnMessage("Server is not available");
        }
        else {
            sendDataToUIListener.returnMessage("Socket is not ready.");
        }
    }

    public void setISendDataToUIListener(ISendDataToUIListener ISendDataToUIListener){
        this.sendDataToUIListener = ISendDataToUIListener;
    }

    public void sendMyUsername() throws IOException {
        /*
        MessageSender messageSender = new MessageSender();
        messageSender.setISendDataToUIListener(sendDataToUIListener);
        messageSender.execute("[Username]-"+et_username.getText());
        */
        Communication.getInstance().sendMessage("[Username]-"+et_username.getText());

        //Log.d(TAG,"Username sent in sendMyUsername.");
        Client.getInstance().setUsername(et_username.getText()+"");
        //send.setVisibility(View.VISIBLE);
        et_username.setText("");
        //connect.setVisibility(View.INVISIBLE);
    }

    public void makeWifis(String input){
        String[] parts = input.split("~");
        for(int i=0;i<parts.length;i++){
            String[] data = parts[i].split(" ");
            if(data.length == 4){
                try{
                    wifiList.add(new WiFi(Integer.valueOf(data[0]),data[1],Integer.valueOf(data[2]),Integer.valueOf(data[3])));
                }
                catch (Exception ignored){}
            }
        }
    }

}
