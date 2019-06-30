package ro.ms.sapientia.zsolti.wifimanager;

public class WiFiReference {
    private int id = 0;
    private int floor=0;
    private int x=0;
    private int y=0;
    private String name="";
    private double level=0;
    private double frequency=0;

    public WiFiReference(int floor, int x, int y, String name, double level, double frequency){
        this.floor = floor;
        this.x = x;
        this.y = y;
        this.name = name;
        this.level = level;
        this.frequency = frequency;
    }

    public WiFiReference(int id, int floor, int x, int y, String name, double level, double frequency){
        this.id = id;
        this.floor = floor;
        this.x = x;
        this.y = y;
        this.name = name;
        this.level = level;
        this.frequency = frequency;
    }

    public WiFiReference(){

    }

    public int getFloor() {
        return floor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public double getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return floor + "~" + x + "~" + y + "~" + name + "~" + level + "~" + frequency;
    }
}
