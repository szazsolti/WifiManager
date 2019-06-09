package ro.ms.sapientia.zsolti.wifimanager;

import android.graphics.Point;
import android.util.Log;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Trilateration implements Serializable{


    private static Trilateration single_instance = null;
    private double x;
    private double y;
    private String TAG = "TRILATERATION";

    private Trilateration() {
    }

    private ArrayList<WiFi> ssidDistance = new ArrayList<>();

    /*
    public Trilateration(ArrayList<WiFi> ssidDistance1) {
        ssidDistance = new ArrayList<>(ssidDistance1);
    }*/

    public static Trilateration getInstance(){
        if(single_instance==null){
            single_instance = new Trilateration();
        }
        return single_instance;
    }

    public void setSsidDistance(ArrayList<WiFi> ssidDistance) {
        this.ssidDistance = ssidDistance;
    }

    private Point router1 = new Point();
    private Point router2 = new Point();
    private Point router3 = new Point();


    private double r1 = 0, r2 = 0, r3 = 0;
    private double d = 0;
    private double ex = 0; // router1-bol router2-be mutato egysegvektor
    private double i = 0; // x komponens nagysaga
    private double ey = 0; // y iranyba vett egysegvektor
    private double ez =0; // a ket vektor kozotti szog szinusza
    private double j = 0; // y komponens nagysaga


    public void setR (){
        r1=ssidDistance.get(0).getDistance();
        r2=ssidDistance.get(1).getDistance();
        r3=ssidDistance.get(2).getDistance();
    }

    public void setParameters(){
        d = Math.sqrt(Math.pow(router2.x - router1.x, 2) + Math.pow(router2.y - router1.y, 2));
        ex = (router2.x - router1.x) / d; // router1-bol router2-be mutato egysegvektor
        i = ex * (router3.x - router1.x); // x komponens nagysaga
        ey = (router3.y - router1.y - ex * ex * router3.y) /
                (Math.sqrt(Math.pow(router3.x - router1.x - ex * ex * router3.x, 2.0) +
                        Math.pow(router3.y - router1.y - ex * ex * router3.y, 2.0))); // y iranyba vett egysegvektor
        ez = Math.abs(ex) * Math.abs(ey) * 1; // a ket vektor kozotti szog szinusza
        j = ey * (router3.y - router1.y); // y komponens nagysaga

    }

    public void setRouter(int r1x, int r1y, int r2x, int r2y, int r3x, int r3y){
        //Log.d(TAG, "setRouter: r1x: " + r1x + " r1y: " + r1y + " r2x" + r2x + " r2y: " + r2y + " r3x: " + r3x + " r3y: " + r3y);
        router1.set(r1x,r1y); //in cm
        router2.set(r2x,r2y); //in cm
        router3.set(r3x,r3y); //in cm
    }


    private double getX1(){
        return ((Math.pow(r1, 2) - Math.pow(r2, 2) + Math.pow(d, 2)) / 2 * d) / 10000;
    }

    private double getY1(){
        return (Math.pow(r1, 2) - Math.pow(r3, 2) + Math.pow(i, 2) + Math.pow(j, 2)) /
                (2 * j) - (i / j) * getX1();
    }

    private double getZ(){
        return Math.sqrt(Math.abs(Math.pow(r1, 2) - Math.pow(getX1(), 2) - Math.pow(getY1(), 2)));
    }

    public static double round(double d, int decimalPlace) {
        try {
            BigDecimal bigdecimal = new BigDecimal(Double.toString(d));
            bigdecimal = bigdecimal.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
            return bigdecimal.doubleValue();
        } catch (Exception e) {
            return 0;
        }
    }
    /*
    private double round(double d, int decimalPlace) {
        return Double.parseDouble((d+"").substring(0,((d+"").indexOf(".")+decimalPlace+1)));
    }*/

    public void calculateX(){
        x=round(router1.x + (getX1() * ex + getY1() * ey + getZ() * ez), 3);
        //Log.d(TAG, "calculateX: x: " + x);
    }

    public void calculateY(){
        y=round(router1.y + (getX1() * ex + getY1() * ey + getZ() * ez), 3);
        //Log.d(TAG, "calculateY: y: " + y);
    }

    public double getX(){
        calculateX();
        return x;
    }

    public double getY(){
        calculateY();
        return y;
    }

    public void notifyToDraw(String message){
        if(message.equals("calculate")){
            calculateX();
            calculateY();
            //INotifyToDraw();
        }
    }

/*
    public void setGetMessageToDraw(GetMessageToDraw getMessageToDraw){
        this.getMessageToDraw=getMessageToDraw;
    }*/
}
