package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

public interface ConnectionListener {

    void onConnectionEstablished();

    void onConnectionFailed();

    void onConnectionClosed();

    void onCOnnectionInterrupted();
}
