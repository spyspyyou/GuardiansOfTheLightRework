package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import testing.gotl.spyspyyo.bluetoothtesting.UI.App;


/**
 * Created by Sandro on 08/09/2016.
 */
public class ConnectionManager {
    private static ArrayList<Connection> connections;
    private static ConnectionManagementThread connectionManagementThread;

    private static void startConnectionManagementThread(){
        if (connectionManagementThread.running){
            Log.w("ConnectionManager", "ConnectionManagementThread is already running.");
            return;
        }
        stopConnectionManagementThread();
        connectionManagementThread = new ConnectionManagementThread();
        connectionManagementThread.start();
    }

    private static void stopConnectionManagementThread(){
        try {
            connectionManagementThread = null;
            connectionManagementThread.join();
        } catch (InterruptedException e) {
            Log.w("ConnectionManager", "ConnectionManagementThread was interrupted.");
            e.printStackTrace();
        }
        connectionManagementThread = null;
    }

    protected void addConnection(Connection pConnection){
        connections.add(pConnection);
    }


    public static class ConnectionManagementThread extends Thread{
        private final int CONNECTION_CHECK_RATE = 5000;
        private boolean running = true;

        @Override
        public void run() {
            while(running&&!connections.isEmpty()){
                try {
                    for (Connection connection:connections){
                        //checkStatus
                    }
                    sleep(CONNECTION_CHECK_RATE);
                } catch (InterruptedException e) {
                    Log.e("CMThread", "Thread was interrupted.");
                    e.printStackTrace();
                }
            }
            running = false;
        }
    }

    private class Connection {
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
    }

    private class ClientConnectionCreateThread extends Thread implements UUIDS{
        private final BluetoothDevice BLUETOOTH_DEVICE;

        /**
         * This Thread is started automatically upon creation!
         * @param pBluetoothDevice - the remote device to connect to
         */
        public ClientConnectionCreateThread(BluetoothDevice pBluetoothDevice){
            BLUETOOTH_DEVICE = pBluetoothDevice;
            start();
        }
        @Override
        public void run() {
            BluetoothSocket bluetoothSocket = null;
            for (UUID uuid:UUIDS) {
                try {
                    bluetoothSocket = BLUETOOTH_DEVICE.createRfcommSocketToServiceRecord(uuid);
                    bluetoothSocket.connect();
                } catch (IOException e) {
                    if (bluetoothSocket == null)Log.e("Connection", "Failed to create BluetoothSocket");
                    else {
                        Log.e("Connection", "Failed to connect the BluetoothSocket");
                        try {
                            bluetoothSocket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    e.printStackTrace();
                }
            }
            if (bluetoothSocket.isConnected()){
                addConnection(new Connection(BLUETOOTH_DEVICE, bluetoothSocket));
                Log.i("ClientCCThread", "Connection successful");
            }else{
                Toast.makeText(App.getContext(), "Failed to Create Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ServerConnectionCreateThread extends Thread{

    }
}
