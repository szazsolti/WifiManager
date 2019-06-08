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
import android.widget.Button;

import java.util.ArrayList;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.IDrawerLocker;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendWiFiListFromManagerToWiFiReferencePointsFragment;
import ro.ms.sapientia.zsolti.wifimanager.MainActivity;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.PinchZoomPan;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.ReferencePoint;
import ro.ms.sapientia.zsolti.wifimanager.WiFi;
import ro.ms.sapientia.zsolti.wifimanager.WiFiReference;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.log;


public class WiFiReferencePointsFragment extends Fragment {

    private Context context;
    private PinchZoomPan pinchZoomPan;
    private ArrayList<Point> points = new ArrayList<>();
    private Paint paint = new Paint();
    private ArrayList<ReferencePoint> referencePointsFromDatabase = new ArrayList<>();
    private ArrayList<WiFi> wifiListFromDevice = new ArrayList<>();
    private Thread refreshWifi = new Thread();
    private String TAG = "WIFIREFERENCEPOINTSFRAGMENT";

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

        Uri selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/elso_emelet");
        pinchZoomPan = view.findViewById(R.id.ivImage);
        pinchZoomPan.setContext(context);
        pinchZoomPan.loadImageOnCanvas(selectedImage);

        //referencePointsFromDatabase = Manager.getInstance().getReferencePointsFromDatabase();

        paint.setColor(Color.BLACK);

        for(ReferencePoint it : referencePointsFromDatabase){
            Point point = new Point();

            point.x = it.getReferenceWifis().get(0).getX();
            point.y = it.getReferenceWifis().get(0).getY();

            //Log.d(TAG, "onCreateView: " + "x: " + point.x + " y: " + point.y);

            points.add(point);
        }

        refreshWiFiList();

        pinchZoomPan.drawPoints(points, paint);


        return view;
    }

    private void refreshWiFiList(){
        refreshWifi = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    wifiListFromDevice=Manager.getInstance().getWifisFromDevice();
                    calculateConvolution(wifiListFromDevice,referencePointsFromDatabase);
                    Log.d(TAG, "run: wifiListFromDevice" + wifiListFromDevice.size());
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

    private void calculateConvolution(ArrayList<WiFi> wifiListFromDevice, ArrayList<ReferencePoint> referencePointsFromDatabase){
        wifiListFromDevice.get(0).getLevel();
        referencePointsFromDatabase.get(0).getReferenceWifis().get(0).getLevel();
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

        pinchZoomPan.drawUser(referencePointsFromDatabase.get(index).getReferenceWifis().get(0).getX(),referencePointsFromDatabase.get(index).getReferenceWifis().get(0).getY());
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

    public void startListWifisToSetReference(){
        ListWiFisToSetReferenceFragment searchWifiFragment = new ListWiFisToSetReferenceFragment(context);
        //searchWifiFragment.setArguments(bundle);
        //searchWifiFragment.setNotifyToDraw(notifyDraw);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, searchWifiFragment);
        fragmentTransaction.addToBackStack("wifiReferencePointsFragment");
        fragmentTransaction.commit();
    }

    public void setReferencePointsFromDatabaseList(ArrayList<ReferencePoint> referencePointsFromDatabase){
        this.referencePointsFromDatabase = referencePointsFromDatabase;
    }

}
