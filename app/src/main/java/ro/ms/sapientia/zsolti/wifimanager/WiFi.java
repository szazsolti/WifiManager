package ro.ms.sapientia.zsolti.wifimanager;

import java.io.Serializable;

import static java.lang.Math.ceil;

public class WiFi implements Serializable {

    private int ID = 0;
    private String name = "";
    private int x = 0;
    private int y = 0;
    private double distance=0;
    private double percentage=0;
    private double frequency=0;
    private double level=0;

    public WiFi(){
    }

    public WiFi(String name, double level, double frequency){
        this.name=name;
        this.level=level;
        this.frequency=frequency;
    }

    public WiFi(int id, String name, int x, int y){
        this.ID=ID;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        if(ceil(ID) == ID) {
            this.ID = ID;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        if(ceil(x) == x){
            this.x = x;
        }
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        if(ceil(y) == y){
            this.y = y;
        }
    }

    public double getDistance(){
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "WiFi{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", distance=" + distance +
                ", percentage=" + percentage +
                ", frequency=" + frequency +
                ", level=" + level +
                '}';
    }
}
