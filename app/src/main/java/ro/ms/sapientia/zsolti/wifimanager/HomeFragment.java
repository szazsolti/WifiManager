package ro.ms.sapientia.zsolti.wifimanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import ro.ms.sapientia.zsolti.wifimanager.Communication.MessageSender;
import ro.ms.sapientia.zsolti.wifimanager.Communication.ReaderThread;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.GetMessageInFragment;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.GetMessageListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.NotifyToDraw;

public class HomeFragment extends Fragment implements GetMessageInFragment {

    private OnFragmentInteractionListener mListener;
    private TextView tw_test;
    private Button bt_connect;
    private EditText et_username;
    private Thread readerThread;
    private GetMessageListener getMessageListener;
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

        ReaderThread readerThread = new ReaderThread();
        readerThread.setGetMessageListener(getMessageListener);
        readerThread.setGetMessageInFragment(this);
        this.readerThread = new Thread(readerThread);
        //((MainActivity)Objects.requireNonNull(getActivity())).setGetMessageInFragment((GetMessageInFragment) getContext());
        HomeFragment.this.readerThread.start();
        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMyUsername();
                //((MainActivity)Objects.requireNonNull(getActivity())).setGetMessageInFragment((GetMessageInFragment) getContext());
            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void returnMessage(String text) {
        processingMessage(text);
    }

    public void processingMessage(String message){
        String[] parts = message.split("-");
        if(message.equals("OK")){
            getMessageListener.returnMessage("Waiting for data...");
        }
        else if(parts[0].equals("[Wifis]")){
             makeWifis(parts[1]);
             if(wifiList.size()>=3){

                 Bundle bundle = new Bundle();
                 bundle.putSerializable("wifilist", wifiList);
                 SearchWifiFragment searchWifiFragment = new SearchWifiFragment(context);
                 searchWifiFragment.setArguments(bundle);
                 //searchWifiFragment.setNotifyToDraw(notifyDraw);
                 FragmentManager fragmentManager = getFragmentManager();
                 FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                 fragmentTransaction.replace(R.id.fragment_container, searchWifiFragment);

                 //fragmentTransaction.addToBackStack("homeFragment");
                 fragmentTransaction.commit();
             }
             else{
                 getMessageListener.returnMessage("Not enough wifi.");
             }
        }
        else if(message.equals("Exit")){
            getMessageListener.returnMessage("Server is not available");
        }
        else {
            getMessageListener.returnMessage("Socket is not ready.");
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void setGetMessageListener(GetMessageListener getMessageListener){
        this.getMessageListener = getMessageListener;
    }

    public void sendMyUsername(){
        MessageSender messageSender = new MessageSender();
        messageSender.setGetMessageListener(getMessageListener);
        messageSender.execute("[Username]-"+et_username.getText());
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
