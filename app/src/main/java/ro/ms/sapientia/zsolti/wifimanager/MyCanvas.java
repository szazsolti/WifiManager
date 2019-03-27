package ro.ms.sapientia.zsolti.wifimanager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class MyCanvas extends View {
    Paint p = new Paint();
    Double x_coord;
    Double y_coord;

    public MyCanvas(Context context){
        super(context);
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
            x_coord = x_1;
            y_coord = y_1;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setColor(Color.BLACK);
        Float x = Float.parseFloat(x_coord+"");
        Float y = Float.parseFloat(y_coord+"");
        canvas.drawCircle(x,y,20,p);
    }

}
