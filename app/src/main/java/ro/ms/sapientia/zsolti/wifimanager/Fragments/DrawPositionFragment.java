package ro.ms.sapientia.zsolti.wifimanager.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.NotifyToDraw;
import ro.ms.sapientia.zsolti.wifimanager.MyCanvas;
import ro.ms.sapientia.zsolti.wifimanager.NotifyToDrawBroadcastReceiver;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.Trilateration;
import ro.ms.sapientia.zsolti.wifimanager.UserOnCanvas;


public class DrawPositionFragment extends Fragment implements NotifyToDraw {

    private MyCanvas myCanvas;
    private Point point = new Point();
    private String TAG = "DRAWPOSITIONFRAGMENT";
    private Context context;
    private RelativeLayout relativeLayout;
    private NotifyToDrawBroadcastReceiver receiver = new NotifyToDrawBroadcastReceiver();
    private ISendDataToUIListener sendDataToUIListener;
    private IntentFilter filter = new IntentFilter("draw");

    @SuppressLint("ValidFragment")
    public DrawPositionFragment (Context context){
        this.context = context;
    }

    public DrawPositionFragment() {
        // Required empty public constructor
    }

    public static DrawPositionFragment newInstance() {
        DrawPositionFragment fragment = new DrawPositionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //receiver.setNotifyToDraw(notifyToDraw);

        View view =  inflater.inflate(R.layout.fragment_draw_position, container, false);

        point.set((int) Trilateration.getInstance().getX(),(int)Trilateration.getInstance().getY());

        Log.d(TAG,"X: " + Trilateration.getInstance().getX() + "Y: " + Trilateration.getInstance().getY());

        try{
            context.registerReceiver(receiver, filter);
            receiver.setNotifyToDraw(this);
        }
        catch (Exception e){
            //ISendDataToUIListener.returnMessage("Helytelen hivatkozás. Az alkalmazás kilép.");
            System.exit(2);
        }

        relativeLayout = view.findViewById(R.id.rect);

        UserOnCanvas user = new UserOnCanvas(point.x+"",point.y+"","Me");

        myCanvas = new MyCanvas(context,user);
        //Log.d(TAG,point.x + " " + point.y);

        //myCanvas.setParameters(point.x+"", point.y+"");
        myCanvas.setContext(context);
        myCanvas.setBackgroundResource(R.drawable.szoba2);
        relativeLayout.addView(myCanvas);

        return view;
    }
    void updateCanvasData(){
        point.set((int)Trilateration.getInstance().getX(),(int)Trilateration.getInstance().getY());
        myCanvas.setParameters(point.x+"", point.y+"");
        myCanvas.invalidate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        context.registerReceiver(receiver, filter);
        receiver.setNotifyToDraw(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(receiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void notifyToDraw(String message) {
        Log.d(TAG,"Notified from reciver"+message);
        updateCanvasData();
    }
/*
    @Override
    public void returnMessageToDraw(String text) {

        if(text.equals("draw")){

        }
    }*/

    public void setISendDataToUIListener(ISendDataToUIListener ISendDataToUIListener){
        this.sendDataToUIListener = ISendDataToUIListener;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
