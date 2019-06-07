package ro.ms.sapientia.zsolti.wifimanager.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Communication;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.IDrawerLocker;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromManagerToWiFiReferencePointsFragment;
import ro.ms.sapientia.zsolti.wifimanager.ListWifiItem;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.WiFi;
import ro.ms.sapientia.zsolti.wifimanager.WifiListAdapter;
import ro.ms.sapientia.zsolti.wifimanager.WifiScanReceiver;

import static java.lang.StrictMath.abs;

public class ListWiFisToSetReferenceFragment extends Fragment   {

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private WifiScanReceiver wifiReciever;
    private List<ListWifiItem> listWifiItems;
    private WifiManager mainWifiObj;
    private String TAG = "LISTWIFISTOSETREFERENCE";
    private Button getWifis;
    private Button saveRef;
    private ArrayList<WiFi> wifisFromDevice;
    private EditText floor;
    private EditText x;
    private EditText y;


    @SuppressLint("ValidFragment")
    public ListWiFisToSetReferenceFragment(Context context) {
        this.context=context;
    }

    public ListWiFisToSetReferenceFragment() {
        // Required empty public constructor
    }

    public static ListWiFisToSetReferenceFragment newInstance(String param1, String param2) {
        ListWiFisToSetReferenceFragment fragment = new ListWiFisToSetReferenceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_wifis_to_set_reference, container, false);

        ((IDrawerLocker) getActivity()).setDrawerEnabled(true);

        floor = new EditText(context);
        x = new EditText(context);
        y = new EditText(context);
        //mainWifiObj = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
       // wifiReciever = new WifiScanReceiver(mainWifiObj);
       // wifiReciever.setISendWiFiListFromWiFiScanReceiverToListWiFisToSetReference(this);

        recyclerView = view.findViewById(R.id.recycle_wifis);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        getWifis = view.findViewById(R.id.getList);
        saveRef = view.findViewById(R.id.bt_SaveRef);
        listWifiItems = new ArrayList<>();

        //ListWifiItem item = new ListWifiItem("Ez meg kell jelenjen!");
        //listWifiItems.add(item);

        adapter=new WifiListAdapter(listWifiItems,context);
        recyclerView.setAdapter(adapter);

        getWifis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "getWifis: in onClickListener");
                wifisFromDevice = Manager.getInstance().getWifisFromDevice();
                //Log.d(TAG, "onTouch: "+ wifisFromDevice.toString());
                //Log.d(TAG, "onTouch: tempSize"+wifisFromDevice.size());
                listWifiItems.clear();
                for(int i = 0; i< wifisFromDevice.size(); i++){
                    listWifiItems.add(new ListWifiItem(wifisFromDevice.get(i).getName()+" "+ wifisFromDevice.get(i).getLevel()+" "+ wifisFromDevice.get(i).getFrequency()));
                }
                adapter.notifyDataSetChanged();
            }
        });

        saveRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wifisFromDevice!=null){
                    //Log.d(TAG, "getLevelCoord: in onClickListener");
                    getLevelCoord(v);
                }
            }
        });

        return view;
    }

    private void getLevelCoord(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());//Context is activity context

        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setTitle(getString(R.string.dialog_title));
        //builder.setMessage(getString(R.string.message_item));
        //builder.setTitle(getString(R.string.update));
        //builder.setMessage("");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams xAndYLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        floor.setLayoutParams(lp);
        floor.setHint(getString(R.string.floor_number));
        floor.setTextColor(ContextCompat.getColor(context, R.color.black));
        floor.setInputType(InputType.TYPE_CLASS_NUMBER);

        //floor.setText("String in edit text you want");
        x.setLayoutParams(xAndYLayout);
        y.setLayoutParams(xAndYLayout);

        x.setHint(getString(R.string.x_coord));
        x.setTextColor(ContextCompat.getColor(context, R.color.black));
        x.setInputType(InputType.TYPE_CLASS_NUMBER);

        y.setHint(getString(R.string.y_coord));
        y.setTextColor(ContextCompat.getColor(context, R.color.black));
        y.setInputType(InputType.TYPE_CLASS_NUMBER);

        if(floor.getParent() != null || x.getParent() != null || y.getParent() != null){
            ((ViewGroup)floor.getParent()).removeView(floor);
            ((ViewGroup)x.getParent()).removeView(x);
            ((ViewGroup)y.getParent()).removeView(y);
        }

        layout.addView(floor);
        layout.addView(x);
        layout.addView(y);
        builder.setView(layout);
        //builder.setView(x);
        //builder.setView(y);
        builder.setPositiveButton(getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//Positive button click event
                        if(!floor.getText().toString().equals("") && !x.getText().toString().equals("") && !y.getText().toString().equals("")){
                            for(int i = 0;i< wifisFromDevice.size();i++){
                                try {
                                    Communication.getInstance().sendMessage("[WifiToReference]-"
                                            +wifisFromDevice.size()
                                            +"~"+floor.getText().toString()
                                            +"~"+x.getText().toString()
                                            +"~"+y.getText().toString()
                                            +"~"+wifisFromDevice.get(i).getName()
                                            +"~"+abs(wifisFromDevice.get(i).getLevel())
                                            +"~"+ wifisFromDevice.get(i).getFrequency());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                Communication.getInstance().sendMessage("[WifiToReferenceStop]-");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        builder.setNegativeButton(getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//Negative button click event
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
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
/*
    @Override
    public void returnWiFiListFromDevice(ArrayList<WiFi> wifisFromDevice) {
        //Log.d(TAG, "returnWiFiListFromDevice: "+wifisFromDevice.toString());
        //Log.d(TAG, "WifiListFromDevice: "+ Manager.getInstance().getWifisFromDevice().toString());
        //for(int i=0;i<wifisFromDevice.size();i++){
        //    listWifiItems.add(new ListWifiItem(wifisFromDevice.get(i).getName()+" "+wifisFromDevice.get(i).getLevel()+" "+wifisFromDevice.get(i).getFrequency()));
        //}
        adapter.notifyDataSetChanged();
    }
    */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
