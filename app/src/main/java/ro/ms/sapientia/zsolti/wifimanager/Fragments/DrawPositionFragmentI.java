package ro.ms.sapientia.zsolti.wifimanager.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.IDrawerLocker;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.INotifyToDraw;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.MyCanvasForTrilateration;
import ro.ms.sapientia.zsolti.wifimanager.NotifyToDrawBroadcastReceiver;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.Trilateration;
import ro.ms.sapientia.zsolti.wifimanager.UserOnCanvas;


public class DrawPositionFragmentI extends Fragment implements INotifyToDraw {

    private MyCanvasForTrilateration myCanvasForTrilateration;
    private Point point = new Point();
    private String TAG = "DRAWPOSITIONFRAGMENT";
    private Context context;
    private RelativeLayout relativeLayout;
    private NotifyToDrawBroadcastReceiver receiver = new NotifyToDrawBroadcastReceiver();
    private ISendDataToUIListener sendDataToUIListener;
    private IntentFilter filter = new IntentFilter("draw");

    @SuppressLint("ValidFragment")
    public DrawPositionFragmentI(Context context){
        this.context = context;
    }

    public DrawPositionFragmentI() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_draw_position, container, false);

        ((IDrawerLocker) getActivity()).setDrawerEnabled(true);

        if(Trilateration.getInstance().getX()!=0 && Trilateration.getInstance().getY()!=0){
            point.set((int)Trilateration.getInstance().getX(),(int)Trilateration.getInstance().getY());
        }

        try{
            context.registerReceiver(receiver, filter);
            receiver.setNotifyToDraw(this);
        }
        catch (Exception e){
        }

        relativeLayout = view.findViewById(R.id.rect);

        UserOnCanvas user = new UserOnCanvas(point.x+"", point.y+"", Client.getInstance().getUsername());

        myCanvasForTrilateration = new MyCanvasForTrilateration(context,user);

        myCanvasForTrilateration.setContext(context);
        myCanvasForTrilateration.setBackgroundResource(R.drawable.szoba2);

        relativeLayout.addView(myCanvasForTrilateration);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void updateCanvasData(){
        if(Trilateration.getInstance().getX()!=0 && Trilateration.getInstance().getY()!=0){
            point.set((int)Trilateration.getInstance().getX(),(int)Trilateration.getInstance().getY());
        }
        myCanvasForTrilateration.setParameters(point.x+"", point.y+"");
        myCanvasForTrilateration.invalidate();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
        catch (Exception ignored){}
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            context.unregisterReceiver(receiver);
            receiver = null;
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
        catch (Exception ignored){}
    }

    @Override
    public void notifyToDraw(String message) {
        updateCanvasData();
    }

    public void setISendDataToUIListener(ISendDataToUIListener ISendDataToUIListener){
        this.sendDataToUIListener = ISendDataToUIListener;
    }
}
