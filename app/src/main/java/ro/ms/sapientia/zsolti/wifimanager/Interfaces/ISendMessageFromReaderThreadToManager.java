package ro.ms.sapientia.zsolti.wifimanager.Interfaces;

public interface ISendMessageFromReaderThreadToManager {
    void returnMessageFromReaderThread(String message);
}
