package ro.ms.sapientia.zsolti.wifimanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;

public class PinchZoomPan extends View {
    private Bitmap mBitmap;
    private int mImageWidth;
    private int mImageHeight;
    private int x;
    private int y;
    ArrayList<Point> points = new ArrayList<>();

    private float mPositionX;
    private float mPositionY;
    private float mLastTouchX;
    private float mLastTouchY;

    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerID = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;
    private final static float mMinZoom = 0.5f;
    private final static float mMaxZoom = 5.0f;
    private Paint paint = new Paint();
    public PinchZoomPan(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBitmap != null){
            canvas.save();
            /*
            if ((mPositionX * -1) < 0) {
                mPositionX = 0;
            } else if ((mPositionX * -1) > mImageWidth * mScaleFactor - getWidth()) {
                mPositionX = (mImageWidth * mScaleFactor - getWidth()) * -1;
            }
            if ((mPositionY * -1) < 0) {
                mPositionY = 0;
            } else if ((mPositionY * -1) > mImageHeight * mScaleFactor - getHeight()) {
                mPositionY = (mImageHeight * mScaleFactor - getHeight()) * -1;
            }

            if ((mImageHeight * mScaleFactor) < getHeight()) {
                mPositionY = 0;
            }*/
            canvas.translate(mPositionX,mPositionY);
            canvas.scale(mScaleFactor, mScaleFactor);
            canvas.drawBitmap(mBitmap,0,0,null);
            //canvas.drawCircle(x,y,5,pBlack);

            for(Point p: points){
                canvas.drawCircle(p.x, p.y, 5, paint);
            }
            canvas.save();
            canvas.restore();
        }
    }

    public void drawPoints(ArrayList<Point> points, Paint p){
        this.points = points;
        this.paint = p;
        invalidate();
    }

    public void drawCircle(int x, int y, Paint p){

        //pBlack.setColor(Color.BLACK);
        this.paint = p;
        this.x = x;
        this.y = y;
        invalidate();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //the scale gesture detector should inspect all the touch events
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

        float aspectRatio = (float) bitmap.getHeight()/(float) bitmap.getWidth();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mImageWidth = displayMetrics.widthPixels;
        mImageHeight = Math.round(mImageWidth * aspectRatio);
        mBitmap = bitmap.createScaledBitmap(bitmap,mImageWidth,mImageHeight,false);
        invalidate();
        //requestLayout();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            //don't to let the image get too large or small
            mScaleFactor = Math.max(mMinZoom, Math.min(mScaleFactor, mMaxZoom));

            invalidate();

            return true;
        }
    }
}
