package ro.ms.sapientia.zsolti.wifimanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static java.lang.StrictMath.abs;

public class MyCanvasForReferencePoints extends View {

    private float x_coord=0;
    private float y_coord=0;
    private String TAG = "MYCANVAS";
    private Context context;
    private Paint paint = new Paint();
    private float hightRatio = -1;
    private float widthRatio = -1;
    private int xCm = -1;
    private int yCm = -1;
    private Bitmap mBitmap = null;

    public MyCanvasForReferencePoints(Context context){
        super(context);
        this.context = context;
    }

    public int getxCm() {
        return xCm;
    }

    public int getyCm() {
        return yCm;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.RED);
        canvas.drawBitmap(mBitmap,0,0,null);
        canvas.drawCircle(x_coord,y_coord,10,paint);
        xCm = (int)y_coord * 8560 / mBitmap.getHeight();
        /*
        Az x tengelyen a távolság, y_cord-al számoltam a 90 fokos elforditás miatt
        Képlet: (kiválasztott_pont/Az_egyetem_hossza/Képmagasság)
         */

        yCm = ((int) (mBitmap.getWidth() - x_coord) * 3630 / mBitmap.getWidth());
        /*
        A y tengelyen a távolság, x_cord-al számoltam az elforditás miatt
        Képlet: (kiválasztott_pont/Az_egyetem_szélessége/Képmagasság)
         */
        if(xCm < 0 || yCm < 0){
            Toast.makeText(context,"Csak az beltérben helyezhető el referenciapont!" ,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"xCm: "+ xCm +" yCm: " + yCm ,Toast.LENGTH_SHORT).show();
        }

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void loadImageOnCanvas(Uri selectedImage) {
        Bitmap bitmap = null;

        try {
            bitmap = RotateBitmap(MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),selectedImage),90);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        hightRatio = (((float)(displayMetrics.widthPixels) / (float) (bitmap.getWidth()))-0.5f)*2;
        widthRatio = (((float) (displayMetrics.heightPixels) / (float)(bitmap.getHeight()))-0.0f)*2;
        mBitmap = bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*widthRatio),(int)(bitmap.getHeight()*hightRatio),false);
        mBitmap = bitmap.createScaledBitmap(bitmap,mBitmap.getWidth()/2,mBitmap.getHeight()/2,false);
        hightRatio /= 2;
        widthRatio /= 2;
        invalidate();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG,"Clicked x: " + event.getX() + " clicked y: " + event.getY());
        x_coord=event.getX();
        y_coord=event.getY();
        postInvalidate();
        return super.onTouchEvent(event);
    }

    public void setContext(Context context){
        this.context = context;
    }

}
