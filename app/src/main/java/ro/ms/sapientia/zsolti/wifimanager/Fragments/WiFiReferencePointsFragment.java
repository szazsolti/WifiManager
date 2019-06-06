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

import java.util.ArrayList;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.IDrawerLocker;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.PinchZoomPan;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.ReferencePoint;


public class WiFiReferencePointsFragment extends Fragment {

    private Context context;
    protected PinchZoomPan pinchZoomPan;
    private ArrayList<Point> points = new ArrayList<>();
    private Paint paint = new Paint();
    private ArrayList<ReferencePoint> referencePointsFromDatabase = new ArrayList<>();
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

        pinchZoomPan.loadImageOnCanvas(selectedImage);

        //referencePointsFromDatabase = Manager.getInstance().getReferencePointsFromDatabase();

        for(ReferencePoint it : referencePointsFromDatabase){
            Log.d(TAG, "onCreateView: " + it.toString());
        }

        paint.setColor(Color.BLACK);

        for(ReferencePoint it : referencePointsFromDatabase){
            Point point = new Point();

            point.x = it.getReferenceWifis().get(0).getX();
            point.y = it.getReferenceWifis().get(0).getY();

            Log.d(TAG, "onCreateView: " + "x: " + point.x + " y: " + point.y);

            points.add(point);
        }

/*
        for(int i=0;i<6;i++){
            Point point = new Point();

            point.x = i*50;
            point.y = i*30-25;

            points.add(point);
        }
*/
        pinchZoomPan.drawPoints(points, paint);

        //pinchZoomPan.invalidate();
        return view;
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

}
