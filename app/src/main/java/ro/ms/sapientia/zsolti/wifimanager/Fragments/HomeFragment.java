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
import java.util.Collections;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;
import ro.ms.sapientia.zsolti.wifimanager.Communication.Communication;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.IDrawerLocker;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromHomefragmentToMainActivity;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.WiFi;

public class HomeFragment extends Fragment {

    private TextView tw_test;
    private Button bt_connect;
    private EditText et_username;
    private Thread readerThread = new Thread();
    private Thread managerThread;

    private ISendDataToUIListener sendDataToUIListener;
    //private ISendMessageFromReaderThreadToHomeFragment sendMessageFromReaderThreadToHomeFragment = HomeFragment.this;
    private String TAG="HOMEFRAGMENT";
    private Context context;
    private ArrayList<WiFi> wifiList = new ArrayList<>();
    private ISendMessageFromHomefragmentToMainActivity sendMessageFromHomeFragmentToMainActivity;
    //String message="";


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


        ((IDrawerLocker) getActivity()).setDrawerEnabled(false);

        bt_connect = view.findViewById(R.id.bt_connect);
        tw_test = view.findViewById(R.id.tw_instruction);

        et_username = view.findViewById(R.id.et_username);
        et_username.setText(Client.getInstance().getUsername());

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

                    //Log.d(TAG, "onClick: startCommunication: " + Manager.getInstance().startCommunication());

                    //Log.d(TAG, "onClick: ");

                if(!et_username.getText().toString().equals("")){
                    try {
                        //if(Communication.getInstance().connected()){
                            //Log.d(TAG, "onClick: in if");
                            sendMessageFromHomeFragmentToMainActivity.setMessageFromHomeFragmentToMainActivity(et_username.getText().toString());
                            /*Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        if(!Communication.getInstance().connected()){
                                            //Communication.getInstance().initParams();
                                            Communication.getInstance().startReaderThread();
                                            Manager.getInstance().startCommunication();
                                            sendMyUsername();
                                        }
                                        else {
                                            sendMyUsername();
                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();*/
                            try {
                                Communication.getInstance().execute();
                                sendMyUsername();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                       // }
                       // else{
                       //     sendDataToUIListener.returnMessage("Server is not available.");
                      //  }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    sendDataToUIListener.returnMessage("Username is required.");
                }

                //et_username.setText("");



/*
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
        //Log.d(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d(TAG, "onResume: ");

    }
/*
    @Override
    public void returnMessage(String text) {
        processingMessage(text);
    }
*/

    public void processingMessage(String message){
        String[] parts = message.split("-");

        if(parts[0].equals("[Wifis]")){
             makeWifis(parts[1]);
             if(wifiList.size()>=3){
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("wifilist", wifiList);
                //Manager manager = new Manager(context);

                    Manager manager = Manager.getInstance();
                    manager.setWifiListFromDataBase(wifiList);
                    manager.setFragmentManager(getFragmentManager());
                    manager.setISendDataToUIListener(sendDataToUIListener);
                    managerThread = new Thread(manager);
                    managerThread.start();
                    Log.d(TAG,"WiFilista"+wifiList.toString());

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
        Communication.getInstance().sendMessage("[Username]-"+et_username.getText());
        Client.getInstance().setUsername(et_username.getText()+"");

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

    public void setISendMessageFromHomeFragmentToMainActivity(ISendMessageFromHomefragmentToMainActivity sendMessageFromHomeFragmentToMainActivity){
        this.sendMessageFromHomeFragmentToMainActivity = sendMessageFromHomeFragmentToMainActivity;
    }

}
