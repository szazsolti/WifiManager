package ro.ms.sapientia.zsolti.wifimanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;

public class MyCanvas extends View {

    //private Double x_coord;
    //private Double y_coord;
    private String TAG = "MYCANVAS";
    private Context context;
    private UserOnCanvas userOnCanvas;

    public MyCanvas(Context context, UserOnCanvas userOnCanvas){
        super(context);
        this.userOnCanvas=userOnCanvas;
    }

    public void setParameters(String x, String y){
        double x_1 = 99999999;
        double y_1 = 99999999;

        try {
            x_1 = Double.parseDouble(x);
            y_1 = Double.parseDouble(y);

        }
        catch(NumberFormatException e){
            Log.e("Err",e.getMessage());
        }

        if (x_1 != 99999999 && y_1 != 99999999){
            userOnCanvas.setXCoord(x_1);
            userOnCanvas.setYCoord(y_1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = 1440-Float.parseFloat(userOnCanvas.getXCoord()+"");
        float y = 2560-Float.parseFloat(userOnCanvas.getYCoord()+"");
        if(x != 0.0 && y != 0.0){
            canvas.drawCircle(x,y,20, userOnCanvas.getPoint());
            canvas.drawText(userOnCanvas.getUserName(),x-60,y+90, userOnCanvas.getText());
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(checkX(event.getX()) && checkY(event.getY())){
            int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
            Toast toast = new Toast(context);
            toast.setGravity(Gravity.TOP|Gravity.LEFT, 1440-(int)Math.round(userOnCanvas.getXCoord())-280,2560-(int) Math.round(userOnCanvas.getYCoord())+(int) Math.round((width/100)*3.472)-20);

            TextView tv = new TextView(context);
            //tv.setBackgroundColor(Color.BLUE);
            tv.setBackground(ContextCompat.getDrawable(context,R.drawable.bubble));
            //tv.setBackgroundColor(Color.parseColor("#7e7e7e"));
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(15);

            Typeface t = Typeface.create("serif", Typeface.BOLD);
            tv.setTypeface(t);
            //tv.setPadding(10,10,10,10);
            tv.setText("  x: " + userOnCanvas.getXCoord() + " y: " + userOnCanvas.getYCoord());
            toast.setView(tv);
            toast.show();

            Log.d(TAG,"Clicked x: " + event.getX() + " clicked y: " + event.getY());
        }
        return super.onTouchEvent(event);
    }

    public void setContext(Context context){
        this.context = context;
    }


    private boolean checkX(float touchedX){
        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        return (1440-userOnCanvas.getXCoord() + (width/100)*3.472 >= touchedX) && (1440-userOnCanvas.getXCoord() - (width/100)*3.472 <= touchedX); //50 pixel, 3,472 szazaleka a kepernyo szelessegenek
    }
    private boolean checkY(float touchedY){
        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        return (2560-userOnCanvas.getYCoord() + (width/100)*3.472 >= touchedY) && (2560-userOnCanvas.getYCoord() - (width/100)*3.472 <= touchedY);
    }
}
