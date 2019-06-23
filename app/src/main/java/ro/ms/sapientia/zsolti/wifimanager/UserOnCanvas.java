package ro.ms.sapientia.zsolti.wifimanager;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import ro.ms.sapientia.zsolti.wifimanager.Communication.Client;

public class UserOnCanvas {
    private Paint point = new Paint();
    private Paint text = new Paint();
    private float xTrilat =0.0f;
    private float yTrilat =0.0f;
    private float xRef = 0.0f;
    private float yRef = 0.0f;
    private String userName;


    public UserOnCanvas(String xTrilat, String yTrilat, String userName){
        float x_1 = 99999999;
        float y_1 = 99999999;

        this.userName=userName;
        point.setColor(Client.getInstance().getClientDotColor());
        text.setColor(Color.RED);
        text.setFakeBoldText(true);
        text.setTextSize(80);
        try {
            x_1 = Float.parseFloat(xTrilat);
            y_1 = Float.parseFloat(yTrilat);

        }
        catch(NumberFormatException e){
            Log.e("Err",e.getMessage());
        }

        if (x_1 != 99999999 && y_1 != 99999999){
            this.xTrilat = x_1;
            this.yTrilat = y_1;
        }
    }

    public UserOnCanvas(String userName, Float xRef, Float yRef){
        this.userName = userName;
        this.xRef = xRef;
        this.yRef = yRef;
        point.setColor(Color.BLACK);
        text.setColor(Color.BLACK);
        //text.setFakeBoldText(true);
        text.setTextSize(80);
    }

    public float getXCoordTrilat() {
        return xTrilat;
    }

    public void setXCoordTrilat(float x_coord) {
        this.xTrilat = x_coord;
    }

    public float getYCoordTrilat() {
        return yTrilat;
    }

    public void setYCoordTrilat(float y_coord) {
        this.yTrilat = y_coord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Paint getPoint() {
        return point;
    }

    public Paint getText() {
        return text;
    }

    public float getXRef() {
        return xRef;
    }

    public void setXRef(float xRef) {
        this.xRef = xRef;
    }

    public float getYRef() {
        return yRef;
    }

    public void setYRef(float yRef) {
        this.yRef = yRef;
    }

}
