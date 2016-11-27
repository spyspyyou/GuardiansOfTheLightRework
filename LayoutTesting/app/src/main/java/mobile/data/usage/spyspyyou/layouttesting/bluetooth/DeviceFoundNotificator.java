package mobile.data.usage.spyspyyou.layouttesting.bluetooth;


public interface DeviceFoundNotificator {

    void notifyChange();

    void discoveryFinished();

    void connectionRequestResult(Connection connection);
}
