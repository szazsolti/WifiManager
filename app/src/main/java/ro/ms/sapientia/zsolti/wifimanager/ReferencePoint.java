package ro.ms.sapientia.zsolti.wifimanager;

import java.util.ArrayList;

public class ReferencePoint {
    private int id = 0;
    private ArrayList<WiFiReference> referenceWifis = new ArrayList<>();

    public ReferencePoint(int Id) {
        this.id = Id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + referenceWifis.toString();
    }

    public void addWifi(WiFiReference wifi){
        referenceWifis.add(wifi);
    }

    public ArrayList<WiFiReference> getReferenceWifis() {
        return referenceWifis;
    }
}
