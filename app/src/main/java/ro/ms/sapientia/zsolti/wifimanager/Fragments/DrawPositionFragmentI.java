package ro.ms.sapientia.zsolti.wifimanager.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ro.ms.sapientia.zsolti.wifimanager.Interfaces.IDrawerLocker;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.INotifyToDraw;
import ro.ms.sapientia.zsolti.wifimanager.Interfaces.ISendDataToUIListener;
import ro.ms.sapientia.zsolti.wifimanager.Manager;
import ro.ms.sapientia.zsolti.wifimanager.MyCanvas;
import ro.ms.sapientia.zsolti.wifimanager.NotifyToDrawBroadcastReceiver;
import ro.ms.sapientia.zsolti.wifimanager.R;
import ro.ms.sapientia.zsolti.wifimanager.Trilateration;
import ro.ms.sapientia.zsolti.wifimanager.UserOnCanvas;


public class DrawPositionFragmentI extends Fragment implements INotifyToDraw {

    private MyCanvas myCanvas;
    private Point point = new Point();
    private String TAG = "DRAWPOSITIONFRAGMENT";
    private Context context;
    private RelativeLayout relativeLayout;
    private NotifyToDrawBroadcastReceiver receiver = new NotifyToDrawBroadcastReceiver();
    private ISendDataToUIListener sendDataToUIListener;
    private IntentFilter filter = new IntentFilter("draw");
    private Toolbar myToolbar;

    private Manager manager = Manager.getInstance();
    @SuppressLint("ValidFragment")
    public DrawPositionFragmentI(Context context){
        this.context = context;
    }

    public DrawPositionFragmentI() {
        // Required empty public constructor
    }

    public static DrawPositionFragmentI newInstance() {
        DrawPositionFragmentI fragment = new DrawPositionFragmentI();
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

        //receiver.setNotifyToDraw(notifyToDraw);


        View view =  inflater.inflate(R.layout.fragment_draw_position, container, false);

        ((IDrawerLocker) getActivity()).setDrawerEnabled(true);

        point.set((int) Trilateration.getInstance().getX(),(int)Trilateration.getInstance().getY());

        //Log.d(TAG,"X: " + Trilateration.getInstance().getX() + "Y: " + Trilateration.getInstance().getY());

        try{
            context.registerReceiver(receiver, filter);
            receiver.setNotifyToDraw(this);
        }
        catch (Exception e){
            //ISendDataToUIListener.returnMessage("Helytelen hivatkozás. Az alkalmazás kilép.");
            System.exit(2);
        }

        relativeLayout = view.findViewById(R.id.rect);
        //myToolbar = view.findViewById(R.id.toolBar);


        //((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(myToolbar);
        UserOnCanvas user = new UserOnCanvas(point.x+"", point.y+"","Me");

        myCanvas = new MyCanvas(context,user);
        //Log.d(TAG,point.x + " " + point.y);
        //myCanvas.setParameters(point.x+"", point.y+"");
        myCanvas.setContext(context);
        myCanvas.setBackgroundResource(R.drawable.szoba2);
        relativeLayout.addView(myCanvas);
        //Log.d(TAG, "onCreateView: ");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    void updateCanvasData(){
        point.set((int)Trilateration.getInstance().getX(),(int)Trilateration.getInstance().getY());
        myCanvas.setParameters(point.x+"", point.y+"");

        //Manager.getInstance().getWifisFromDevice().clear();
        myCanvas.invalidate();
        //Log.d(TAG, "updateCanvasData: WifiListFromDevice: "+ manager.getWifisFromDevice());
        //Log.d(TAG, "updateCanvasData: WiFiListFromDataBase: " + manager.getWifiListFromDataBase());
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            //context.registerReceiver(receiver, filter);
            //receiver.setNotifyToDraw(this);
        }
        catch (Exception ignored){}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Log.d(TAG, "onDestroyView: ");
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
            //Client.getInstance().destroy();
            //Log.d(TAG, "onStop: called");
            //Communication.getInstance().sendMessage("[Logout-]");
           // Communication.getInstance().destroy();
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
        //context.unregisterReceiver(receiver);
        //Log.d(TAG, "onDetach: ");
    }

    @Override
    public void notifyToDraw(String message) {
        //Log.d(TAG,"Notified from reciver"+message);
        updateCanvasData();
    }

    public void setISendDataToUIListener(ISendDataToUIListener ISendDataToUIListener){
        this.sendDataToUIListener = ISendDataToUIListener;
    }
}
