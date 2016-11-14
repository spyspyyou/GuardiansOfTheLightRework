package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import testing.gotl.spyspyyo.bluetoothtesting.global.TODS;

/*package*/ class ConnectionManager {
    //the UUID is one character too short which has to be added when the uuid is used. it defines the index of the connection
    private static final String UUID_STRING = "a810452d-2fda-4113-977e-3494579d3ee";
    private static final byte MAX_CONNECTIONS = 7;
    private static boolean serverAvailable = false;
    private static Connection[] connections = {
            null,
            null,
            null,
            null,
            null,
            null,
            null
    };

    private static ArrayList<AcceptConnectionThread> acceptConnectionThreads = new ArrayList<>();

    public static void startServerAvailability(){
        if (serverAvailable || !AppBluetoothManager.isBluetoothEnabled())return;
        serverAvailable = true;
        Log.e("ConnectionManager", "starting the Server");
        acceptConnectionThreads = new ArrayList<>();
        for(byte i = 0; i < MAX_CONNECTIONS; ++i){
            if (connections[i] == null) acceptConnectionThreads.add(new AcceptConnectionThread(i));
        }
        serverAvailable = true;
    }

    public static void stopServerAvailability(){
        if (serverAvailable = false)return;
        Log.e("ConnectionManager", "stopping the Server");
        serverAvailable  = false;
        while(!acceptConnectionThreads.isEmpty()){
            acceptConnectionThreads.remove(0).cancelAvailability();
        }
    }

    protected static void addConnection(Connection connection){
        if (!hasConnections()){
            new EventSenderThread();
            new EventReceiverThread();
        }
        connections[connection.getIndex()] = connection;
    }

    public static boolean hasConnections(){
        for (Connection c:connections){
            if (c != null)return true;
        }
        return false;
    }

    public static void connect(BluetoothDevice bluetoothDevice){
        new CreateConnectionThread(bluetoothDevice).start();
    }

    public static void disconnect(BluetoothDevice bluetoothDevice){
        Connection connection = getConnectionToDevice(bluetoothDevice);
        if (connection==null)Log.w("ConnectionManager", "Can't disconnect from: " + bluetoothDevice.getAddress() + ", because it is not connected to the App.");
        connection.disconnect();
    }

    public static void disconnect(){
        for (Connection connection:connections){
            if (connection!=null)connection.disconnect();
        }
    }

    private static UUID getUUID (int i){
        return UUID.fromString(UUID_STRING + i);
    }

    private static Connection getConnectionToDevice(BluetoothDevice bluetoothDevice){
        for (Connection connection:connections){
            if (connection.getRemoteDevice()==bluetoothDevice)return connection;
        }
        return null;
    }

    public static Connection[] getConnections(){
        return connections;
    }

    public static void removeDisconnectedConnection(byte index) {
        connections[index] = null;
        if (serverAvailable)acceptConnectionThreads.add(new AcceptConnectionThread(index));
    }

    private static class CreateConnectionThread extends Thread{
        private final BluetoothDevice BLUETOOTH_DEVICE;

        /**
         * This Thread is started automatically upon creation!
         * @param pBluetoothDevice - the remote device to connect to
         */
        public CreateConnectionThread(BluetoothDevice pBluetoothDevice){
            BLUETOOTH_DEVICE = pBluetoothDevice;
            start();
        }

        @Override
        public void run() {
            BluetoothSocket bluetoothSocket = null;
            Log.i("BtTest", "Started connection Thread for: " + BLUETOOTH_DEVICE.getName());
            byte index = 0;
            for (; index < MAX_CONNECTIONS; ++index) {
                Log.i("BtTest", "Attempting connection with index " + index);
                try {
                    bluetoothSocket = BLUETOOTH_DEVICE.createRfcommSocketToServiceRecord(getUUID(index));
                } catch (IOException e) {
                    Log.e("Connection", "Failed to create BluetoothSocket");
                    e.printStackTrace();
                    continue;
                }
                try {
                    bluetoothSocket.connect();
                    break;
                } catch (IOException e) {
                    Log.e("BtTest", "Failed to connect with index " + index);
                    e.printStackTrace();
                }
            }
            if (bluetoothSocket == null || !bluetoothSocket.isConnected()) {
                Log.e("BtTest", "Failed to connect to " + BLUETOOTH_DEVICE.getName() + ":" + BLUETOOTH_DEVICE.getAddress());
                //todo: hanle failure, inform the issuer
            }else {
                addConnection(new Connection(bluetoothSocket, index));
                Log.i("BtTest", "Connection to " + BLUETOOTH_DEVICE.getName() + " successful");
            }
        }
    }

    private static class AcceptConnectionThread extends Thread implements TODS {
        private BluetoothServerSocket bluetoothServerSocket = null;
        private final UUID UUID;
        private final byte INDEX;

        public AcceptConnectionThread(byte i){
            INDEX = i;
            UUID = getUUID(i);
            start();
        }

        @Override
        public void run() {
            while(serverAvailable && connections[INDEX]==null) {
                BluetoothSocket bluetoothSocket = null;
                try {
                    bluetoothServerSocket = AppBluetoothManager.getBluetoothServerSocket(UUID);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("BtTest", "Could not open the bluetoothServerSocket with uuid " + UUID.toString());
                }

                if (bluetoothServerSocket == null) {
                    Log.i("BtTest", "Failed to get the bluetoothServerSocket with uuid " + UUID.toString());
                    continue;
                }

                try {
                    bluetoothSocket = bluetoothServerSocket.accept();
                } catch (IOException e) {
                    if (serverAvailable)Log.e("ConnectionServerThread", "failed to accept the connection");
                    e.printStackTrace();
                    continue;
                }

                if (bluetoothSocket == null || !bluetoothSocket.isConnected()) {
                    Log.e("ConnectionServerThread", "failed to connect correctly");
                    break;
                }
                addConnection(new Connection(bluetoothSocket, INDEX));
            }
            cancelAvailability();
        }

        public void cancelAvailability(){
            try {
               if (bluetoothServerSocket!=null)bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            acceptConnectionThreads.remove(this);
        }
    }
}
