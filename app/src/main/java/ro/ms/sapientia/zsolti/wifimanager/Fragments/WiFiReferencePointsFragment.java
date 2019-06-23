package ro.ms.sapientia.zsolti.wifimanager.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;
import ro.ms.sapientia.zsolti.wifimanager.Communication.Communication;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.IDrawerLocker;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendMessageFromManagerToWiFiReferencePointsFragment;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.PinchZoomPan;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.ReferencePoint;
import ro.ms.sapientia.zsolti.wifimanager.UserOnCanvas;
import ro.ms.sapientia.zsolti.wifimanager.WiFi;
import ro.ms.sapientia.zsolti.wifimanager.WiFiReference;

import static java.lang.Math.max;
import static java.lang.StrictMath.abs;


public class WiFiReferencePointsFragment extends Fragment implements ISendMessageFromManagerToWiFiReferencePointsFragment {

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
    private int selectedFloor=0;
    private ISendDataToUIListener sendDataToUIListener;

    public WiFiReferencePointsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public WiFiReferencePointsFragment(Context context){
        this.context=context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Manager.getInstance().setISendMessageFromManagerToWiFiReferencePointsFragment(this);
        makeQuery();
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
                        //selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/foldszint_100");
                        //pinchZoomPan.loadImageOnCanvas(selectedImage);
                        //referencePointsFromDatabase.clear();
                        //makeQuery(0);
                        changeFloorPicture(0);
                        //setPointsToFloor(0);
                        setPointsToFloor();
                        break;
                    case 1:
                        //Log.d(TAG, "onItemClick: selected 1");
                        //selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/elso_emelet_200");
                        //pinchZoomPan.loadImageOnCanvas(selectedImage);
                        //referencePointsFromDatabase.clear();
                        //makeQuery(1);
                        //selectedFloor = 1;
                        changeFloorPicture(1);
                        setPointsToFloor();
                        //setPointsToFloor(1);
                        break;
                    case 2:
                        //Log.d(TAG, "onItemClick: selected 2");
                        //selectedImage = Uri.parse("android.resource://"+context.getPackageName()+ "/drawable/masodik_emelet_300");
                        //pinchZoomPan.loadImageOnCanvas(selectedImage);
                        //referencePointsFromDatabase.clear();
                        //selectedFloor = 2;
                        changeFloorPicture(2);
                        setPointsToFloor();
                        //makeQuery(2);

                        //setPointsToFloor(2);
                        break;
                    case 3:
                        //Log.d(TAG, "onItemClick: selected 3");
                        //selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/harmadik_emelet_400");
                        //pinchZoomPan.loadImageOnCanvas(selectedImage);
                        //referencePointsFromDatabase.clear();
                        //selectedFloor = 3;
                        changeFloorPicture(3);
                        setPointsToFloor();
                        //makeQuery(3);
                        //setPointsToFloor(3);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //referencePointsFromDatabase = Manager.getInstance().getReferencePointsFromDatabase();

        paintUsers.setColor(Client.getInstance().getOnlineUsersDotColor());
        paintReference.setColor(Client.getInstance().getReferencePointDotColor());
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
                String hashCode=Communication.getInstance().hashCode()+"";
                while (true) {
                    //Log.d(TAG, "run: " + Communication.getInstance().hashCode());
                    if(Communication.getInstance()==null || Communication.getInstance().isCancelled() || !hashCode.equals(Communication.getInstance().hashCode()+"")){
                        break;
                    }
                    wifiListFromDevice=Manager.getInstance().getWifisFromDevice();
                    if(referencePointsFromDatabase.size()!=0){
                        calculateCorrelation(wifiListFromDevice,new ArrayList<>(referencePointsFromDatabase));
                    }
                    onlineUsers = Manager.getInstance().getOnlineUsers();
                    Log.d(TAG, "run: onlineUsers: " + onlineUsers.size());
                    pinchZoomPan.drawUsers(onlineUsers,paintUsers);
                    //pinchZoomPan.postInvalidate();
                    //setPointsToFloor();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        refreshWifi.start();
    }

    private void makeQuery(){
        Communication.getInstance().sendMessage("[ReferenceWifiPointsFromDatabase]-");
    }

    private void changeFloorPicture(int floor){
        switch (floor){
            case 0:
                selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/foldszint_100");
                pinchZoomPan.loadImageOnCanvas(selectedImage);
                selectedFloor = floor;
                break;
            case 1:
                selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/elso_emelet_200");
                pinchZoomPan.loadImageOnCanvas(selectedImage);
                selectedFloor = floor;
                break;
            case 2:
                selectedImage = Uri.parse("android.resource://"+context.getPackageName()+ "/drawable/masodik_emelet_300");
                pinchZoomPan.loadImageOnCanvas(selectedImage);
                selectedFloor = floor;
                break;
            case 3:
                selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/harmadik_emelet_400");
                pinchZoomPan.loadImageOnCanvas(selectedImage);
                selectedFloor = floor;
                break;
        }
    }

    private void setPointsToFloor(){
        points.clear();
        for(ReferencePoint it : referencePointsFromDatabase){
            Log.d(TAG, "setPointsToFloor: " + it.getReferenceWifis().get(0).getFloor());
            if(it.getReferenceWifis().get(0).getFloor() == selectedFloor){
                Point point = new Point();

                point.x = it.getReferenceWifis().get(0).getX();
                point.y = it.getReferenceWifis().get(0).getY();

                //Log.d(TAG, "onCreateView: " + "x: " + point.x + " y: " + point.y);
                points.add(point);
            }
        }
        pinchZoomPan.drawPoints(new ArrayList<>(points), paintReference);
    }


    private void calculateCorrelation(ArrayList<WiFi> wifiListFromDevice, ArrayList<ReferencePoint> referencePointsFromDatabase){
        //wifiListFromDevice.get(0).getLevel();
        //referencePointsFromDatabase.get(0).getReferenceWifis().get(0).getLevel();
        //ArrayList<Double> result = new ArrayList<>();
        double[] result;
        double max = 0;
        //int zeroNumbers = 9999;
        int i=0;
        ReferencePoint bestPoint = null;
        result = new double[referencePointsFromDatabase.size()];
        //result[]=0.0;
        for(ReferencePoint rp : referencePointsFromDatabase){
            //max[j]=0.0;
            //int numberOfElements = rp.getReferenceWifis().size() + wifiListFromDevice.size();
            result[i]=0;
            for(int n=0;n<rp.getReferenceWifis().size();n++){
                for(int k=0;k<wifiListFromDevice.size();k++){
                    if(rp.getReferenceWifis().get(n).getName()
                            .equals(wifiListFromDevice.get(k).getName())){
                        result[i]=result[i]
                                +abs( rp.getReferenceWifis().get(n).getLevel())
                                * abs((wifiListFromDevice.get(k).getLevel()));
                    }
                }
            }
            //Log.d(TAG, "calculateCorrelation: " + getMax(result) + " zero: " + countZeros(result));

            if(result[i]>max){
                max = result[i];
                //zeroNumbers = countZeros(result);
                bestPoint = rp;
            }
            i++;
        }

        if(bestPoint != null){
            Client.getInstance().setXRef(bestPoint.getReferenceWifis().get(0).getX()+"");
            Client.getInstance().setYRef(bestPoint.getReferenceWifis().get(0).getY()+"");

            if(Client.getInstance().getAutoFloor()){
                changeFloorPicture(bestPoint.getReferenceWifis().get(0).getFloor());
                try {
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selectFloor.setSelection(selectedFloor);
                        }
                    });
                }
                catch (Exception e){}
            }
            pinchZoomPan.drawUser(bestPoint.getReferenceWifis().get(0).getX(),bestPoint.getReferenceWifis().get(0).getY());
            setPointsToFloor();
            Communication.getInstance().sendMessage("[PositionConv]-"+bestPoint.getReferenceWifis().get(0).getX() + " " + bestPoint.getReferenceWifis().get(0).getY());
        }else{
            try{
                sendDataToUIListener.returnMessage("No reference point found.");
            }
            catch (Exception e){};
        }
    }
/*
    private double getMax(double[] array){
        try {
            double max=array[0];
            for (double anArray : array) {
                if (anArray > max) {
                    max = anArray;
                }
            }
            return max;
        }catch (Exception e){
            return 0;
        }
    }

    private int countZeros(double[] array){
        try {
            int counter = 0;
            for (double anArray : array) {
                if (anArray == 0) {
                    counter++;
                }
            }
            return counter;
        }catch (Exception e){
            return 9999;
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
*/
    public void setReferencePointsFromDatabaseList(ArrayList<ReferencePoint> referencePointsFromDatabase){
        this.referencePointsFromDatabase = referencePointsFromDatabase;
    }
    public void setISendDataToUIListener(ISendDataToUIListener ISendDataToUIListener){
        this.sendDataToUIListener = ISendDataToUIListener;
    }

    @Override
    public void drawReferncePoints(boolean received) {
        if(received){
            setPointsToFloor();
        }
    }
}
