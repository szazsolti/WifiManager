package ro.ms.sapientia.zsolti.wifimanager;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class UserOnCanvas {
    private Paint point = new Paint();
    private Paint text = new Paint();
    private Double x_coord;
    private Double y_coord;
    private String userName;

    public UserOnCanvas(String x, String y, String userName){
        double x_1 = 99999999;
        double y_1 = 99999999;

        this.userName=userName;
        point.setColor(Color.BLACK);
        text.setColor(Color.RED);
        text.setFakeBoldText(true);
        text.setTextSize(80);
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

    public Double getXCoord() {
        return x_coord;
    }

    public void setXCoord(Double x_coord) {
        this.x_coord = x_coord;
    }

    public Double getYCoord() {
        return y_coord;
    }

    public void setYCoord(Double y_coord) {
        this.y_coord = y_coord;
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
}
