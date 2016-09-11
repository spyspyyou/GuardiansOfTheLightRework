package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Sandro on 11.09.2016.
 */
public class Connection {
    private final InputStream INPUT_STREAM;
    private final OutputStream OUTPUT_STREAM;
    private final BluetoothDevice BLUETOOTH_DEVICE;
    private final BluetoothSocket BLUETOOTH_SOCKET;

    public Connection(BluetoothDevice pBluetoothDevice, BluetoothSocket pBluetoothSocket){
        BLUETOOTH_DEVICE = pBluetoothDevice;
        BLUETOOTH_SOCKET = pBluetoothSocket;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = pBluetoothSocket.getInputStream();
            outputStream = pBluetoothSocket.getOutputStream();
        } catch (IOException e) {
            Log.e("Connection", "failed to get data streams");
            e.printStackTrace();
        }
        INPUT_STREAM = inputStream;
        OUTPUT_STREAM = outputStream;
    }

    public static Connection newServerConnection(BluetoothSocket sBluetoothSocket){
        return new Connection(sBluetoothSocket.getRemoteDevice(), sBluetoothSocket);
    }

    public BluetoothDevice getBluetoothDevice(){
        return BLUETOOTH_DEVICE;
    }

    public boolean isHostingGame(){
        String bluetoothName = BLUETOOTH_DEVICE.getName();
        char c = bluetoothName.charAt(bluetoothName.indexOf('_'));
        return (c!='0');
    }
}
