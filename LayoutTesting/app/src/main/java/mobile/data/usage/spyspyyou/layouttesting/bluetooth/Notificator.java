package mobile.data.usage.spyspyyou.layouttesting.bluetooth;


import android.bluetooth.BluetoothDevice;

public interface Notificator {

    void notifyChange();

    void discoveryFinished();

    void connectionRequestResult(BluetoothDevice bluetoothDevice);

    void playerLeft(String address);
}
