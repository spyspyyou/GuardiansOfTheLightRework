package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import testing.gotl.spyspyyo.bluetoothtesting.UI.App;
import testing.gotl.spyspyyo.bluetoothtesting.global.TODS;


/**
 * Created by Sandro on 08/09/2016.
 */
public class ConnectionManager implements UUIDS{
    private static ArrayList<Connection> connections;
    private static ArrayList<ServerConnectionCreateThread> serverConnectionCreateThreads;
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

    protected static void addConnection(Connection pConnection){
        connections.add(pConnection);
    }

    public static ArrayList<BluetoothDevice> getGameHostingDevices(){
        ArrayList<BluetoothDevice> gameHostConnections = new ArrayList<>();
        for (Connection connection:connections){
            if (connection.isHostingGame())gameHostConnections.add(connection.getBluetoothDevice());
        }
        return gameHostConnections;
    }


    public static void setupServerAvailability(){
        serverConnectionCreateThreads = new ArrayList<>();
        for(UUID uuid:UUIDS){
            serverConnectionCreateThreads.add(new ServerConnectionCreateThread(uuid));
        }
    }

    public static void stopServerAvailability(){
        while(!serverConnectionCreateThreads.isEmpty()){
            serverConnectionCreateThreads.remove(0).cancelAvailability();
        }
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
                } catch (IOException e) {
                    Log.e("Connection", "Failed to create BluetoothSocket");
                    e.printStackTrace();
                    continue;
                }
                try {
                    bluetoothSocket.connect();
                    break;
                } catch (IOException e) {
                    Log.e("Connection", "Failed to connect the BluetoothSocket with uuid " + uuid.toString());
                    e.printStackTrace();
                    continue;
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

    private static class ServerConnectionCreateThread extends Thread implements TODS {
        private BluetoothServerSocket bluetoothServerSocket = null;
        private UUID uuid;

        public ServerConnectionCreateThread(UUID sUuid){
            uuid = sUuid;
            start();
        }

        @Override
        public void run() {
            BluetoothSocket bluetoothSocket = null;
            try {
                if (allowInsecureConnections)
                    bluetoothServerSocket = BluetoothManagerIntern.getInsecureBluetoothServerSocket(uuid);
                else bluetoothServerSocket = BluetoothManagerIntern.getBluetoothServerSocket(uuid);
            }catch(IOException e){
                e.printStackTrace();
                App.toast("Could not open the bluetoothServerSocket with uuid " + uuid.toString());
            }

            if (bluetoothServerSocket == null){
                App.toast("Failed to get the bluetoothServerSocket with uuid " + uuid.toString());
            }else{

            }

            try {
                bluetoothSocket = bluetoothServerSocket.accept();
            } catch (IOException e) {
                Log.e("ConnectionServerThread", "failed to accept the connection");
                e.printStackTrace();
            }

            if (bluetoothSocket == null){
                Log.e("ConnectionServerThread", "failed to connect correctly");
                return;
            }

            addConnection(Connection.newServerConnection(bluetoothSocket));
            cancelAvailability();
        }

        public void cancelAvailability(){
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
