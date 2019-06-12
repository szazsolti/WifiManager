package ro.ms.sapientia.zsolti.wifimanager.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Inflater;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;
import ro.ms.sapientia.zsolti.wifimanager.Communication.Communication;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.IDrawerLocker;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromManagerToWiFiReferencePointsFragment;
import ro.ms.sapientia.zsolti.wifimanager.MainActivity;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.PinchZoomPan;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.ReferencePoint;
import ro.ms.sapientia.zsolti.wifimanager.UserOnCanvas;
import ro.ms.sapientia.zsolti.wifimanager.WiFi;
import ro.ms.sapientia.zsolti.wifimanager.WiFiReference;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.log;


public class WiFiReferencePointsFragment extends Fragment {

    private Context context;
    private PinchZoomPan pinchZoomPan;
    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<Point> userPoints = new ArrayList<>();
    private Paint paintReference = new Paint();
    private Paint paintUsers = new Paint();
    private ArrayList<ReferencePoint> referencePointsFromDatabase = new ArrayList<>();
    private ArrayList<WiFi> wifiListFromDevice = new ArrayList<>();
    private ArrayList<UserOnCanvas> onlineUsers = new ArrayList<>();
    private Thread refreshWifi = new Thread();
    private Spinner selectFloor;
    private Uri selectedImage;
    private String TAG = "WIFIREFERENCEPOINTSFRAGMENT";
    private ISendDataToUIListener sendDataToUIListener;

    public WiFiReferencePointsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public WiFiReferencePointsFragment(Context context){
        this.context=context;
    }

    public static WiFiReferencePointsFragment newInstance(String param1, String param2) {
        WiFiReferencePointsFragment fragment = new WiFiReferencePointsFragment();
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
        View view = inflater.inflate(R.layout.fragment_wi_fi_reference_points, container, false);

        ((IDrawerLocker) getActivity()).setDrawerEnabled(true);
        pinchZoomPan = view.findViewById(R.id.ivImage);
        pinchZoomPan.setContext(context);

        selectFloor = view.findViewById(R.id.floorSelect);

        String[] floors = new String[]{"Ground floor","First","Second","Third"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item,floors);
        selectFloor.setAdapter(adapter);

        selectFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //Log.d(TAG, "onItemClick: selected 0");
                        //sendDataToUIListener.returnMessage("No data from this floor.");
                        selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/foldszint_100");
                        pinchZoomPan.loadImageOnCanvas(selectedImage);

                        setPointsToFloor(0);
                        break;
                    case 1:
                        //Log.d(TAG, "onItemClick: selected 1");
                        selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/elso_emelet_200");
                        pinchZoomPan.loadImageOnCanvas(selectedImage);
                        setPointsToFloor(1);
                        break;
                    case 2:
                        //Log.d(TAG, "onItemClick: selected 2");
                        selectedImage = Uri.parse("android.resource://"+context.getPackageName()+ "/drawable/masodik_emelet_300");
                        pinchZoomPan.loadImageOnCanvas(selectedImage);
                        setPointsToFloor(2);
                        break;
                    case 3:
                        //Log.d(TAG, "onItemClick: selected 3");
                        selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/harmadik_emelet_400");
                        pinchZoomPan.loadImageOnCanvas(selectedImage);
                        setPointsToFloor(3);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //referencePointsFromDatabase = Manager.getInstance().getReferencePointsFromDatabase();

        paintUsers.setColor(Color.BLUE);
        paintReference.setColor(Color.GREEN);
        refreshWiFiList();


/*
        for(UserOnCanvas uc : onlineUsers){
            Point point = new Point();

            point.x = Integer.parseInt(uc.getXRef()+"");
            point.y = Integer.parseInt(uc.getYRef()+"");

            userPoints.add(point);
        }
*/

        return view;
    }

    private void refreshWiFiList(){
        refreshWifi = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    wifiListFromDevice=Manager.getInstance().getWifisFromDevice();
                    calculateConvolution(wifiListFromDevice,referencePointsFromDatabase);
                    onlineUsers = Manager.getInstance().getOnlineUsers();
                    Log.d(TAG, "run: onlineUsers: " + onlineUsers.size());
                    pinchZoomPan.drawUsers(onlineUsers,paintUsers);
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        refreshWifi.start();
    }

    private void setPointsToFloor(int floor){
        points.clear();
        for(ReferencePoint it : referencePointsFromDatabase){
            Log.d(TAG, "setPointsToFloor: " + it.getReferenceWifis().get(0).getFloor());
            if(it.getReferenceWifis().get(0).getFloor() == floor){
                Point point = new Point();

                point.x = it.getReferenceWifis().get(0).getX();
                point.y = it.getReferenceWifis().get(0).getY();

                //Log.d(TAG, "onCreateView: " + "x: " + point.x + " y: " + point.y);
                points.add(point);
            }
        }
        pinchZoomPan.drawPoints(points, paintReference);
    }


    private void calculateConvolution(ArrayList<WiFi> wifiListFromDevice, ArrayList<ReferencePoint> referencePointsFromDatabase){
        //wifiListFromDevice.get(0).getLevel();
        //referencePointsFromDatabase.get(0).getReferenceWifis().get(0).getLevel();
        //ArrayList<Double> result = new ArrayList<>();
        Double[] result;
        Double[] max = new Double[referencePointsFromDatabase.size()];
        int j = 0;
        for(ReferencePoint rp : referencePointsFromDatabase){
            max[j]=0.0;
            int numberOfElements = rp.getReferenceWifis().size() + wifiListFromDevice.size();

            Double[] x = makeDoubleListFromReferenceWifi(rp.getReferenceWifis(),numberOfElements);
            Double[] y = makeDoubleListFromDeviceWifi(wifiListFromDevice,numberOfElements);
            //Double max=0.0;
            //int N = x.length + y.length;
            //Log.d(TAG, "calculateConvolution: " + numberOfElements);
            result = new Double[numberOfElements];
            for(int n =0;n<numberOfElements;n++){
                result[n]=0.0;
                for(int k=0;k<n;k++){
                    if((n-k)>y.length){
                        y[n-k]=0.0;
                    }
                    if(k>x.length){
                        x[k]=0.0;
                    }
                    //Log.d(TAG, "calculateConvolution: n:" + n + " k: " + k);
                    result[n] = result[n] + x[k]*y[n-k];
                }
            }
            for (int i =0;i<result.length;i++){
                if(result[i]>max[j]){
                    max[j] = result[i];
                    //Log.d(TAG, "calculateConvolution: Max: " + max + " point x: " + rp.getReferenceWifis().get(0).getX() + " y: " + rp.getReferenceWifis().get(0).getY());
                }
                //Log.d(TAG, "calculateConvolution: " + result[i]);
            }
            j++;
        }
        double maxx = 0.0;
        int index=0;
        for(int i=0;i<referencePointsFromDatabase.size();i++){
            if(max[i]>maxx){
                maxx = max[i];
                index = i;
            }
        }
        /*
        Log.d(TAG, "calculateConvolution: max: " + maxx + "x: " +
                referencePointsFromDatabase.get(index).getReferenceWifis().get(0).getX() +
                " y: " + referencePointsFromDatabase.get(index).getReferenceWifis().get(0).getY());
*/
        Client.getInstance().setXRef(referencePointsFromDatabase.get(index).getReferenceWifis().get(0).getX()+"");
        Client.getInstance().setYRef(referencePointsFromDatabase.get(index).getReferenceWifis().get(0).getY()+"");
        pinchZoomPan.drawUser(referencePointsFromDatabase.get(index).getReferenceWifis().get(0).getX(),referencePointsFromDatabase.get(index).getReferenceWifis().get(0).getY());
        try {
            Communication.getInstance().sendMessage("[PositionConv]-"+referencePointsFromDatabase.get(index).getReferenceWifis().get(0).getX() + " " + referencePointsFromDatabase.get(index).getReferenceWifis().get(0).getY());
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private Double[] makeDoubleListFromReferenceWifi(ArrayList<WiFiReference> wiFiReferences, int N){
        Double[] list = new Double[N];
        for(int i=0;i<N;i++){
            if(i>=wiFiReferences.size()){
                list[i]=0.0;
            }
            else{
                list[i]=wiFiReferences.get(i).getLevel();
            }
            //Log.d(TAG, "makeDoubleListFromReferenceWifi: " + list[i]);
        }
        return list;
    }

    private Double[] makeDoubleListFromDeviceWifi(ArrayList<WiFi> wifiFromDevice, int N){
        Double[] list = new Double[N];
        for(int i=0;i<N;i++){
            if(i>=wifiFromDevice.size()){
                list[i] = 0.0;
            }
            else {
                list[i]=abs(wifiFromDevice.get(i).getLevel());
            }
            //Log.d(TAG, "makeDoubleListFromDeviceWifi: " + list[i]);
        }
        return list;
    }

    public void setReferencePointsFromDatabaseList(ArrayList<ReferencePoint> referencePointsFromDatabase){
        this.referencePointsFromDatabase = referencePointsFromDatabase;
    }
    public void setISendDataToUIListener(ISendDataToUIListener ISendDataToUIListener){
        this.sendDataToUIListener = ISendDataToUIListener;
    }

}
