package ro.ms.sapientia.zsolti.wifimanager.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Communication;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.IDrawerLocker;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromManagerToWiFiReferencePointsFragment;
import ro.ms.sapientia.zsolti.wifimanager.ListWifiItem;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.MyCanvasForReferencePoints;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.ReferencePoint;
import ro.ms.sapientia.zsolti.wifimanager.WiFi;
import ro.ms.sapientia.zsolti.wifimanager.WifiListAdapter;
import ro.ms.sapientia.zsolti.wifimanager.WifiScanReceiver;

import static java.lang.StrictMath.abs;

public class ListWiFisToSetReferenceFragment extends Fragment   {

    private Context context;
    private String TAG = "LISTWIFISTOSETREFERENCE";
    private ArrayList<WiFi> wifisFromDevice;
    private RelativeLayout layout;
    private MyCanvasForReferencePoints myCanvasForReferencePoints;
    private Button selectFloor;
    private Button saveButton;
    private Uri selectedImage;
    private Spinner selectFloorSpinner;

    @SuppressLint("ValidFragment")
    public ListWiFisToSetReferenceFragment(Context context) {
        this.context=context;
        selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/foldszint_100");
    }

    public ListWiFisToSetReferenceFragment() {
        selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/foldszint_100");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_wifis_to_set_reference, container, false);
        layout = view.findViewById(R.id.rect);
        myCanvasForReferencePoints = new MyCanvasForReferencePoints(context);
        layout.addView(myCanvasForReferencePoints);
        myCanvasForReferencePoints.loadImageOnCanvas(selectedImage);
        selectFloor = view.findViewById(R.id.floorSelectButton);
        saveButton = view.findViewById(R.id.saveButton);
        selectFloorSpinner = new Spinner(context);
        selectFloorSpinner.setSelection(0);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifisFromDevice = Manager.getInstance().getWifisFromDevice();
                //Log.d(TAG, "onClick: wifisFromDeive: " + wifisFromDevice.size());
                int x = myCanvasForReferencePoints.getxCm();
                int y =myCanvasForReferencePoints.getyCm();

                if(x>0 && y>0){
                    //Log.d(TAG, "onClick: selected item index: "+selectFloorSpinner.getSelectedItemPosition());
                    for(int i = 0;i< wifisFromDevice.size();i++){
                        Communication.getInstance().sendMessage("[WifiToReference]-"
                                +wifisFromDevice.size()
                                +"~"+selectFloorSpinner.getSelectedItemPosition()
                                +"~"+x
                                +"~"+y
                                +"~"+wifisFromDevice.get(i).getName()
                                +"~"+abs(wifisFromDevice.get(i).getLevel())
                                +"~"+ wifisFromDevice.get(i).getFrequency());
                    }
                    Communication.getInstance().sendMessage("[WifiToReferenceStop]-");
                }

            }
        });
        selectFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLevelCoord(v);
            }
        });
        return view;
    }


    private void getLevelCoord(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());//Context is activity context

        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        builder.setTitle(getString(R.string.dialog_title));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams xAndYLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        String[] floors = new String[]{"Ground floor","First","Second","Third"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,floors);
        selectFloorSpinner.setAdapter(adapter);
/*
        selectFloorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/foldszint_100");
                        break;
                    case 1:
                        selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/elso_emelet_200");
                        break;
                    case 2:
                        selectedImage = Uri.parse("android.resource://"+context.getPackageName()+ "/drawable/masodik_emelet_300");
                        break;
                    case 3:
                        selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/harmadik_emelet_400");
                        break;
                    default:
                        Log.e(TAG,"nincs kiválasztva semmi!");
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

*/

        if(selectFloorSpinner.getParent() != null){
            ((ViewGroup)selectFloorSpinner.getParent()).removeView(selectFloorSpinner);
        }

        layout.addView(selectFloorSpinner);
        builder.setView(layout);

        builder.setPositiveButton(getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (selectFloorSpinner.getSelectedItemPosition()){
                            case 0:
                                selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/foldszint_100");
                                break;
                            case 1:
                                selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/elso_emelet_200");
                                break;
                            case 2:
                                selectedImage = Uri.parse("android.resource://"+context.getPackageName()+ "/drawable/masodik_emelet_300");
                                break;
                            case 3:
                                selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/harmadik_emelet_400");
                                break;
                        }
                        myCanvasForReferencePoints.loadImageOnCanvas(selectedImage);
                    }
                });
        builder.setNegativeButton(getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
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
