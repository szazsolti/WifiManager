package ro.ms.sapientia.zsolti.wifimanager.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromWiFiScanReceiverToListWiFisToSetReference;
import ro.ms.sapientia.zsolti.wifimanager.ListWifiItem;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.WiFi;
import ro.ms.sapientia.zsolti.wifimanager.WifiListAdapter;
import ro.ms.sapientia.zsolti.wifimanager.WifiScanReceiver;

public class ListWiFisToSetReference extends Fragment implements ISendWiFiListFromWiFiScanReceiverToListWiFisToSetReference {

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private WifiScanReceiver wifiReciever;
    private List<ListWifiItem> listWifiItems;
    private WifiManager mainWifiObj;
    private String TAG = "LISTWIFISTOSETREFERENCE";
    private Button getWifis;

    @SuppressLint("ValidFragment")
    public ListWiFisToSetReference(Context context) {
        this.context=context;
    }

    public ListWiFisToSetReference() {
        // Required empty public constructor
    }

    public static ListWiFisToSetReference newInstance(String param1, String param2) {
        ListWiFisToSetReference fragment = new ListWiFisToSetReference();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_wifis_to_set_reference, container, false);

        //mainWifiObj = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
       // wifiReciever = new WifiScanReceiver(mainWifiObj);
       // wifiReciever.setISendWiFiListFromWiFiScanReceiverToListWiFisToSetReference(this);

        recyclerView = view.findViewById(R.id.recycle_wifis);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        getWifis = view.findViewById(R.id.getList);

        listWifiItems = new ArrayList<>();

        //ListWifiItem item = new ListWifiItem("Ez meg kell jelenjen!");
        //listWifiItems.add(item);

        adapter=new WifiListAdapter(listWifiItems,context);
        recyclerView.setAdapter(adapter);


        getWifis.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ArrayList<WiFi> temp = new ArrayList<>();
                temp = Manager.getInstance().getWifisFromDevice();
                Log.d(TAG, "onTouch: tempSize"+temp.size());
                for(int i=0;i<temp.size();i++){
                    listWifiItems.add(new ListWifiItem(temp.get(i).getName()+" "+temp.get(i).getLevel()+" "+temp.get(i).getFrequency()));
                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {

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
    public void returnWiFiListFromDevice(ArrayList<WiFi> wifisFromDevice) {
        Log.d(TAG, "returnWiFiListFromDevice: "+wifisFromDevice.toString());
        Log.d(TAG, "WifiListFromDevice: "+ Manager.getInstance().getWifisFromDevice().toString());
        /*for(int i=0;i<wifisFromDevice.size();i++){
            listWifiItems.add(new ListWifiItem(wifisFromDevice.get(i).getName()+" "+wifisFromDevice.get(i).getLevel()+" "+wifisFromDevice.get(i).getFrequency()));
        }*/
        adapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
