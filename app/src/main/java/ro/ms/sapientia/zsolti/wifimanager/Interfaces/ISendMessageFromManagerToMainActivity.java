package ro.ms.sapientia.zsolti.wifimanager.Interfaces;

import java.util.ArrayList;

import ro.ms.sapientia.zsolti.wifimanager.ReferencePoint;

public interface ISendMessageFromManagerToMainActivity {
    void messageFromManagerToMainActivity(ArrayList<ReferencePoint> referencePointsFromDatabase);
}
