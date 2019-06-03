package ro.ms.sapientia.zsolti.wifimanager.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Objects;

import ro.ms.sapientia.zsolti.wifimanager.PinchZoomPan;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.WiFiManagerSuperClass;


public class WiFiReferencePointsFragment extends Fragment {

    private Context context;
    protected PinchZoomPan pinchZoomPan;
    private Toolbar myToolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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

        Uri selectedImage = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/elso_emelet");
        pinchZoomPan = view.findViewById(R.id.ivImage);

        pinchZoomPan.loadImageOnCanvas(selectedImage);

       // myToolbar = view.findViewById(R.id.toolBar);
        //drawerLayout = view.findViewById(R.id.drawer_layout);
       // navigationView = view.findViewById(R.id.navigationView);



        //((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(myToolbar);
        return view;
    }

    public void startListWifisToSetReference(){
        ListWiFisToSetReference searchWifiFragment = new ListWiFisToSetReference(context);
        //searchWifiFragment.setArguments(bundle);
        //searchWifiFragment.setNotifyToDraw(notifyDraw);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, searchWifiFragment);
        fragmentTransaction.addToBackStack("wifiReferencePointsFragment");
        fragmentTransaction.commit();
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
