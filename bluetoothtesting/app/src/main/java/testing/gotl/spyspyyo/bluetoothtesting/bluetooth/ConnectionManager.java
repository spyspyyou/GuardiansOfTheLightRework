package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import testing.gotl.spyspyyo.bluetoothtesting.global.App;
import testing.gotl.spyspyyo.bluetoothtesting.teststuff.TODS;

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

    /*package*/ static void startServerAvailability(){
        if (serverAvailable || !AppBluetoothManager.isBluetoothEnabled())return;
        serverAvailable = true;
        Log.i("ConnectionManager", "starting the Server");
        acceptConnectionThreads = new ArrayList<>();
        for(byte i = 0; i < MAX_CONNECTIONS; ++i){
            if (connections[i] == null) acceptConnectionThreads.add(new AcceptConnectionThread(i));
        }
        serverAvailable = true;
    }

    /*package*/ static void stopServerAvailability(){
        if (!serverAvailable)return;
        Log.i("ConnectionManager", "stopping the Server");
        serverAvailable  = false;
        while(!acceptConnectionThreads.isEmpty()){
            acceptConnectionThreads.remove(0).cancelAvailability();
        }
        CreateConnectionThread.cancelConnections();
    }

    /*package*/ static BluetoothDevice getBluetoothDevice(String address){
        Connection connection = getConnection(address);
        if (connection == null)return null;
        return connection.getRemoteDevice();
    }

    /*package*/ static boolean hasConnection(String address){
        return getConnection(address) != null;
    }

    /*package*/ static boolean hasConnections(){
        for (Connection c:connections){
            if (c != null)return true;
        }
        return false;
    }

    /*package*/ static void connect(BluetoothDevice bluetoothDevice, Notificator deviceFoundNotificator){
        new CreateConnectionThread(bluetoothDevice, deviceFoundNotificator);
    }

    /*package*/ static void disconnect(String address){
        Connection connection = getConnection(address);
        if (connection == null){
            Log.w("ConnectionManager", "No connection to device trying to disconnect from.");
            return;
        }
        connection.disconnect();
    }

    /*package*/ static void disconnectExcept(String address){
        if (!hasConnection(address))Log.i("ConnectionManager", "the excepted connection is null, disconnecting from all");
        for (Connection connection:connections){
            if (connection != null && !connection.getAddress().equals(address))connection.disconnect();
        }
    }

    /*package*/ static void disconnect(){
        for (Connection connection:connections){
            if (connection!=null){
                connection.disconnect();
            }
        }
    }

    /*package*/ static Connection[] getConnections(){
        return connections;
    }

    /*package*/ static String[] getAddresses(){
        ArrayList<String>addressList = new ArrayList<>();
        for (Connection connection:connections){
            if (connection!=null)addressList.add(connection.getAddress());
        }
        String[]addresses = new String[addressList.size()];
        for(int i = 0; i < addressList.size(); ++i){
            addresses[i] = addressList.get(i);
        }
        return addresses;
    }

    /*package*/ static void send(String[] addressArray, byte[] data) {
        for (String address:addressArray) {
            Connection connection = getConnection(address);
            connection.send(data);
        }
    }

    /*package*/ static void removeDeadConnection(int index){
        if (index >= connections.length ||index < 0)Log.w("ConnectionManager", "connection with index out of bounds: " + index);
        connections[index] = null;
    }

    /*package*/ static void reconnect(Connection connection){
        Log.i("ConnectionManager", "reconnecting: " + connection.getRemoteDevice().getName());
        connection.disconnect();
        connect(connection.getRemoteDevice(), null);
    }

    @Nullable
    private static Connection getConnection(String address){
        for (Connection connection:connections){
            if (connection != null && connection.getRemoteDevice().getAddress().equals(address)){
                return connection;
            }
        }
        Log.w("ConnectionManager", "address has no connection");
        return null;
    }

    private static void addConnection(Connection connection){
        Log.i("ConnectionManager", "adding a connection");
        if (!hasConnections()){
            Log.i("ConnectionManager", "Started the Send/Receive Threads");
            connections[connection.getIndex()] = connection;
            new EventSenderThread();
            new EventReceiverThread();
            return;
        }
        connections[connection.getIndex()] = connection;
    }

    private static UUID getUUID (int i){
        return UUID.fromString(UUID_STRING + i);
    }

    private static class CreateConnectionThread extends Thread {
        private final BluetoothDevice BLUETOOTH_DEVICE;
        private final Notificator DEVICE_FOUND_NOTIFICATOR;
        private static Map<String, CreateConnectionThread> activeCreateConnectionThreads = new HashMap<>();
        BluetoothSocket bluetoothSocket = null;

        /**
         * This Thread is started automatically upon creation!
         *
         * @param pBluetoothDevice - the remote device to connect to
         */
        /*package*/ CreateConnectionThread(BluetoothDevice pBluetoothDevice, Notificator deviceFoundNotificator) {
            BLUETOOTH_DEVICE = pBluetoothDevice;
            DEVICE_FOUND_NOTIFICATOR = deviceFoundNotificator;
            start();
        }

        @Override
        public void run() {
            //todo:remove prepare statement with toasts
            if (activeCreateConnectionThreads.containsKey(BLUETOOTH_DEVICE.getAddress())) {
                Log.i("CCThread", "there is already a Thread for this device");
                return;
            }
            activeCreateConnectionThreads.put(BLUETOOTH_DEVICE.getAddress(), this);
            Looper.prepare();
            AppBluetoothManager.cancelSearch();
            Log.i("CCThread", "Started connection Thread for: " + BLUETOOTH_DEVICE.getName());
            byte index = 0;
            for (; index < MAX_CONNECTIONS; ++index) {
                Log.i("CCThread", "Attempting connection with index " + index);
                try {
                    bluetoothSocket = BLUETOOTH_DEVICE.createInsecureRfcommSocketToServiceRecord(getUUID(index));
                } catch (IOException e) {
                    Log.e("CCThread", "Failed to create BluetoothSocket");
                    e.printStackTrace();
                    continue;
                }
                try {
                    bluetoothSocket.connect();
                    break;
                } catch (IOException e) {
                    Log.w("CCThread", "Failed to connect with index " + index);
                    e.printStackTrace();
                }
            }
            if (bluetoothSocket == null || !bluetoothSocket.isConnected()) {
                Log.e("CCThread", "Failed to connect " + BLUETOOTH_DEVICE.getName() + ": " + BLUETOOTH_DEVICE.getAddress());
                if (DEVICE_FOUND_NOTIFICATOR != null) DEVICE_FOUND_NOTIFICATOR.connectionRequestResult(null);
            } else {
                Log.i("CCThread", "Connection to " + BLUETOOTH_DEVICE.getName() + " successful");
                addConnection(new Connection(bluetoothSocket, index));
                if (DEVICE_FOUND_NOTIFICATOR != null) DEVICE_FOUND_NOTIFICATOR.connectionRequestResult(connections[index].getRemoteDevice());
            }
            activeCreateConnectionThreads.remove(BLUETOOTH_DEVICE.getAddress());
        }

        public static void cancelConnections(){
            Set<String> keys = activeCreateConnectionThreads.keySet();
            for (String key:keys){
                try {
                    activeCreateConnectionThreads.get(key).bluetoothSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class AcceptConnectionThread extends Thread implements TODS {
        private BluetoothServerSocket bluetoothServerSocket = null;
        private final UUID UUID;
        private final byte INDEX;

        /*package*/ AcceptConnectionThread(byte i){
            INDEX = i;
            UUID = getUUID(i);
            start();
        }

        @Override
        public void run() {
            //todo:remove with toasts
            Looper.prepare();
            while(serverAvailable && connections[INDEX]==null) {
                BluetoothSocket bluetoothSocket;
                try {
                    bluetoothServerSocket = AppBluetoothManager.getBluetoothServerSocket(UUID);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("ACThread", "Could not open the bluetoothServerSocket with uuid " + UUID.toString());
                    continue;
                }

                if (bluetoothServerSocket == null) {
                    Log.i("ACThread", "Failed to get the bluetoothServerSocket with uuid " + UUID.toString());
                    continue;
                }

                try {
                    bluetoothSocket = bluetoothServerSocket.accept();
                } catch (IOException e) {
                    if (serverAvailable)Log.e("ACThread", "failed to accept the connection with index: " + INDEX);
                    e.printStackTrace();
                    continue;
                }

                if (bluetoothSocket == null || !bluetoothSocket.isConnected()) {
                    Log.e("ACThread", "failed to connect correctly");
                    break;
                }
                Log.i("ACThread", "Connected to " + bluetoothSocket.getRemoteDevice().getName());
                addConnection(new Connection(bluetoothSocket, INDEX));
            }
            cancelAvailability();
        }

        /*package*/ void cancelAvailability(){
            try {
                if (bluetoothServerSocket!=null)bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            acceptConnectionThreads.remove(this);
        }
    }
}
