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
    private Thread managerThread;

    private ISendDataToUIListener sendDataToUIListener;
    private String TAG="HOMEFRAGMENT";
    private Context context;
    private ArrayList<WiFi> wifiList = new ArrayList<>();
    private ISendMessageFromHomefragmentToMainActivity sendMessageFromHomeFragmentToMainActivity;

    @SuppressLint("ValidFragment")
    public HomeFragment(Context context) {
        this.context=context;
    }

    public HomeFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);


        ((IDrawerLocker) getActivity()).setDrawerEnabled(false);

        bt_connect = view.findViewById(R.id.bt_connect);
        tw_test = view.findViewById(R.id.tw_instruction);

        et_username = view.findViewById(R.id.et_username);
        et_username.setText(Client.getInstance().getUsername());

        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!et_username.getText().toString().equals("")){
                    try {
                            sendMessageFromHomeFragmentToMainActivity.setMessageFromHomeFragmentToMainActivity(et_username.getText().toString());
                        Communication.getInstance().execute();
                        sendMyUsername();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    sendDataToUIListener.returnMessage("Username is required.");
                }
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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setISendDataToUIListener(ISendDataToUIListener ISendDataToUIListener){
        this.sendDataToUIListener = ISendDataToUIListener;
    }


    public void sendMyUsername(){
        Communication.getInstance().sendMessage("[Username]-"+et_username.getText());
        Client.getInstance().setUsername(et_username.getText()+"");

    }

    public void setISendMessageFromHomeFragmentToMainActivity(ISendMessageFromHomefragmentToMainActivity sendMessageFromHomeFragmentToMainActivity){
        this.sendMessageFromHomeFragmentToMainActivity = sendMessageFromHomeFragmentToMainActivity;
    }

}
