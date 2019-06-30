package ro.ms.sapientia.zsolti.wifimanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.StrictMath.abs;

public class MyCanvasForTrilateration extends View {
    private String TAG = "MYCANVAS";
    private Context context;
    private UserOnCanvas userOnCanvas;
    private float x=getWidth()/2f;
    private float y=getHeight()/2f;

    public MyCanvasForTrilateration(Context context, UserOnCanvas userOnCanvas){
        super(context);
        this.userOnCanvas=userOnCanvas;
    }

    public void setParameters(String x, String y){
        float x_1 = 99999999;
        float y_1 = 99999999;

        try {
            x_1 = Float.parseFloat(x);
            y_1 = Float.parseFloat(y);

        }
        catch(NumberFormatException e){
            Log.e("Err",e.getMessage());
        }

        if (x_1 != 99999999 && x_1 != 0 &&  y_1 != 99999999 && y_1 !=0){
            userOnCanvas.setXCoordTrilat(x_1);
            userOnCanvas.setYCoordTrilat(y_1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(userOnCanvas.getXCoordTrilat()!=0 && userOnCanvas.getYCoordTrilat()!=0){
            float xRatio = getWidth()/500;
            float referenceRatioX = getWidth()/userOnCanvas.getXCoordTrilat();
            float screenRatioX = abs(xRatio - referenceRatioX);
            x = (x*0.7f) + (getWidth() - abs(screenRatioX * getWidth())%getWidth())*0.3f;

            float yRatio = getHeight()/630;
            float referenceRatioY = getHeight()/userOnCanvas.getYCoordTrilat();
            float screenRatioY = abs(yRatio - referenceRatioY)/10;
            y = (y*0.7f) + (getHeight()-abs(screenRatioY * getHeight())%getHeight())*0.3f;
        }

        if(x != 0f && y != 0f){
            canvas.drawCircle(x,y,20, userOnCanvas.getPoint());
            canvas.drawText(userOnCanvas.getUserName(),x-60,y+90, userOnCanvas.getText());
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(checkX(event.getX()) && checkY(event.getY())){
            Toast toast = new Toast(context);
            toast.setGravity(Gravity.BOTTOM|Gravity.END, getWidth()-(int)x-250,getHeight() - (int)y-100);

            TextView tv = new TextView(context);
            tv.setBackground(ContextCompat.getDrawable(context,R.drawable.bubble));
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(15);

            Typeface t = Typeface.create("serif", Typeface.BOLD);
            tv.setTypeface(t);
            tv.setText("  x: " + userOnCanvas.getXCoordTrilat() + " y: " + userOnCanvas.getYCoordTrilat());
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
        int width  = getWidth();
        return (x + (width/100)*3.472 >= touchedX) && (x - (width/100)*3.472 <= touchedX); //50 pixel, 3,472 szazaleka a kepernyo szelessegenek
    }
    private boolean checkY(float touchedY){
        int width  = getWidth();
        return (y + (width/100)*3.472 >= touchedY) && (y - (width/100)*3.472 <= touchedY);
    }
}
