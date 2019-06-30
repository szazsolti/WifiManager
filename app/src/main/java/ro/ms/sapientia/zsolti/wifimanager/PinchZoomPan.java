package ro.ms.sapientia.zsolti.wifimanager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;

import static java.lang.StrictMath.abs;

public class PinchZoomPan extends View {
    private Bitmap mBitmap;
    private int xUser = -1;
    private int yUser = -1;
    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<UserOnCanvas> onlineUsers = new ArrayList<>();
    private Paint paintReferencePoint = new Paint();
    private Paint paintUser = new Paint();
    private Paint paintUsers = new Paint();
    private Context context;

    private float mPositionX;
    private float mPositionY;
    private float mLastTouchX;
    private float mLastTouchY;
    private float imageX=1384;
    private float imageY=637;

    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerID = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;
    private final static float mMinZoom = 0.5f;
    private final static float mMaxZoom = 5.0f;

    private String TAG = "PINCHZOOMPAN";

    public PinchZoomPan(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBitmap != null && points!=null){
            canvas.translate(mPositionX,mPositionY);
            canvas.scale(mScaleFactor, mScaleFactor);
            canvas.drawBitmap(mBitmap,0,0,null);

            for(Point p: points){
                if(p != null){
                    float x = calculateX(p.x);
                    float y = calculateY(p.y);
                    canvas.drawCircle(x, y, 5, paintReferencePoint);
                }
            }

            paintUser.setColor(Client.getInstance().getClientDotColor());

            if(xUser != -1 && yUser != -1){
                canvas.drawCircle(calculateX(xUser),calculateY(yUser),8,paintUser);
                canvas.drawText(Client.getInstance().getUsername(),calculateX(xUser)-20,calculateY(yUser)+25,paintUser);
            }
            for(UserOnCanvas p : onlineUsers){
                if(!p.getUserName().equals(Client.getInstance().getUsername())){
                    canvas.drawCircle(calculateX((int)p.getXRef()),calculateY((int)p.getYRef()),5,paintUsers);
                    canvas.drawText(p.getUserName(),calculateX((int)p.getXRef())-20,calculateY((int)p.getYRef())+25,paintUsers);
                }
            }
        }
    }

    public void setContext(Context context){
        this.context = context;
    }

    private float calculateX(int xCoord){
        float x = (imageX*xCoord)/8560;
        return x;
    }

    private float calculateY(int yCoord){
        float y = (imageY*yCoord)/3630;
        return y;
    }

    public void drawPoints(ArrayList<Point> points, Paint p){
        this.points = points;
        this.paintReferencePoint = p;
        postInvalidate();
    }

    public void drawUser(int x, int y){
        this.xUser = x;
        this.yUser = y;
        postInvalidate();
    }

    public void drawUsers(ArrayList<UserOnCanvas> onlineUsers, Paint p){
        this.onlineUsers = onlineUsers;
        this.paintUsers = p;
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleDetector.onTouchEvent(event);
        final int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK){

            case MotionEvent.ACTION_DOWN: {

                //get x and y where we touch the display
                final float x = event.getX();
                final float y = event.getY();

                //remember where touch event started
                mLastTouchX = x;
                mLastTouchY = y;

                //save the ID of this point
                mActivePointerID = event.getPointerId(0);

                break;
            }
            case MotionEvent.ACTION_MOVE:{

                //find the index of the active pointer and fetch its position
                final int pointerIndex = event.findPointerIndex(mActivePointerID);

                final float x = event.getX(pointerIndex);
                final float y = event.getY(pointerIndex);

                if (!mScaleDetector.isInProgress()) {

                    //calculate the distance in x and y directions
                    final float distanceX = x - mLastTouchX;
                    final float distanceY = y - mLastTouchY;

                    mPositionX += distanceX;
                    mPositionY += distanceY;

                    //redraw canvas call onDraw method
                    invalidate();

                }

                //remember this touch position for next move event
                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP:{

                mActivePointerID = INVALID_POINTER_ID;

                break;
            }

            case MotionEvent.ACTION_CANCEL:{

                mActivePointerID = INVALID_POINTER_ID;

                break;
            }

            case MotionEvent.ACTION_POINTER_UP:{
                //extract the index of the pointer that left the screen
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if(pointerId == mActivePointerID){
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = event.getX(newPointerIndex);
                    mLastTouchY = event.getY(newPointerIndex);
                    mActivePointerID = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    public void loadImageOnCanvas(Uri selectedImage) {
        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),selectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageX = bitmap.getWidth();
        imageY = bitmap.getHeight();

        mBitmap = bitmap.createScaledBitmap(bitmap,(int)imageX,(int)imageY,false);
        postInvalidate();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(mMinZoom, Math.min(mScaleFactor, mMaxZoom));
            invalidate();
            return true;
        }
    }
}
