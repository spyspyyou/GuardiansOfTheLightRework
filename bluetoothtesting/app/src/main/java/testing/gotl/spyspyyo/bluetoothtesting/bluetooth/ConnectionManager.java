package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static testing.gotl.spyspyyo.bluetoothtesting.bluetooth.ConnectionManager.CreateConnectionThread.cancelConnecting;
import static testing.gotl.spyspyyo.bluetoothtesting.teststuff.TEST_VARIABLES.TEXT_ENCODING;

/*package*/ class ConnectionManager {
    //the UUID is one character too short which has to be added when the uuid is used. it defines the index of the connection
    private static final byte MAX_CONNECTIONS = 7;

    private static final String BASE_UUID_STRING = "a810452d-2fda-4113-977e-3494579d3ee";
    private static final UUID[] UUID_ARRAY = new UUID[MAX_CONNECTIONS];
    static {
        for (int i = 0; i < MAX_CONNECTIONS; ++i){
            UUID_ARRAY[i] = UUID.fromString(BASE_UUID_STRING + i);
        }
    }

    private static boolean serverActive = false;
    private static Map<String, Connection> connections = new LinkedHashMap<>();

    private static ArrayList<AcceptConnectionThread> acceptConnectionThreads = new ArrayList<>();

    /*package*/ static void startServer(){
        if (serverActive)return;
        cancelConnecting();
        disconnect();
        acceptConnectionThreads.clear();
        serverActive = true;
        for (UUID uuid : UUID_ARRAY) {
            try {
                acceptConnectionThreads.add(new AcceptConnectionThread(uuid));
            } catch (IOException e) {
                Log.e("ConnectionManager", "Could not create AcceptConnectionThread for uuid + " + uuid);
                e.printStackTrace();
            }
        }
        Log.i("ConnectionManager", "started the Server");
    }

    /*package*/ static void stopServer(){
        if (!serverActive)return;
        serverActive = false;
        while(!acceptConnectionThreads.isEmpty()){
            acceptConnectionThreads.remove(0).cancelAvailability();
        }
        Log.i("ConnectionManager", "stopped the Server");
    }

    private static boolean hasConnections(){
        return !connections.isEmpty();
    }

    /*package*/ static void connect(BluetoothDevice bluetoothDevice, @Nullable AppBluetoothManager.ConnectionListener listener){
        try {
            new CreateConnectionThread(bluetoothDevice, listener);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /*package*/ static void disconnect(String address) {
        if (connections.containsKey(address)){
            connections.get(address).close();
        } else {
            Log.w("ConnectionManager", "No connection to device trying to close from.");
        }
    }

    /*package*/ static void disconnect(){
        for (String address:connections.keySet()){
            connections.get(address).close();
        }
    }

    /*package*/ static void addConnection(Connection connection){
        Log.i("ConnectionManager", "adding a connection");
        connections.put(connection.getAddress(), connection);
        new EventSenderThread();
        new EventReceiverThread();
    }

    /*package*/ static void removeClosedConnection(Connection connection){
        if (connections.containsKey(connection.getAddress()))
            connections.remove(connection.getAddress());
    }

    /*package*/ static class CreateConnectionThread extends Thread {

        private static Map<String, CreateConnectionThread> activeCreateConnectionThreads = new HashMap<>();

        private final BluetoothDevice BLUETOOTH_DEVICE;
        private final AppBluetoothManager.ConnectionListener LISTENER;

        BluetoothSocket bluetoothSocket = null;

        private CreateConnectionThread(BluetoothDevice bluetoothDevice, @Nullable AppBluetoothManager.ConnectionListener listener) throws IllegalArgumentException {
            BLUETOOTH_DEVICE = bluetoothDevice;
            LISTENER = listener;
            if (connections.get(bluetoothDevice.getAddress()) != null || activeCreateConnectionThreads.containsKey(BLUETOOTH_DEVICE.getAddress()))
                throw new IllegalArgumentException("There is either already a connection to this address or a Thread trying to connect to it");
            activeCreateConnectionThreads.put(BLUETOOTH_DEVICE.getAddress(), this);
            start();
        }

        @Override
        public void run() {
            Log.d("CCThread", "Started connection Thread for: " + BLUETOOTH_DEVICE.getName());
            for (byte index = 0; index < UUID_ARRAY.length; ++index) {
                Log.d("CCThread", "Attempting connection using uuid with index " + index);
                try {
                    bluetoothSocket = BLUETOOTH_DEVICE.createRfcommSocketToServiceRecord(UUID_ARRAY[index]);
                } catch (IOException e) {
                    Log.e("CCThread", "Failed to create BluetoothSocket for uuid with index " + index);
                    e.printStackTrace();
                    continue;
                }
                try {
                    bluetoothSocket.connect();
                    new Connection(bluetoothSocket, LISTENER);
                    break;
                } catch (IOException e) {
                    Log.w("CCThread", "Failed to connect using uuid with index " + index);
                    e.printStackTrace();
                }
            }

            if (bluetoothSocket == null) {
                Log.e("CCThread", "Failed to connect to " + BLUETOOTH_DEVICE.getName() + ", MAC = " + BLUETOOTH_DEVICE.getAddress());
                if (LISTENER != null)LISTENER.onConnectionFailed();
            }
            activeCreateConnectionThreads.remove(BLUETOOTH_DEVICE.getAddress());
        }

        /*package*/ static void cancelConnecting(){
            for (String key:activeCreateConnectionThreads.keySet()){
                try {
                    activeCreateConnectionThreads.remove(key).bluetoothSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class AcceptConnectionThread extends Thread {

        private BluetoothServerSocket bluetoothServerSocket = null;
        private final UUID UUID;

        /*package*/ AcceptConnectionThread(UUID uuid) throws IOException{
            UUID = uuid;
            bluetoothServerSocket = AppBluetoothManager.getBluetoothServerSocket(UUID);
            start();
        }

        @Override
        public void run() {
            BluetoothSocket bluetoothSocket;
            while(serverActive) {
                try {
                    bluetoothSocket = bluetoothServerSocket.accept();
                } catch (IOException e) {
                    if (serverActive) {
                        Log.e("ACThread", "failed to accept a connection with uuid " + UUID + ". Continuing...");
                        e.printStackTrace();
                        continue;
                    }
                    break;
                }
            }

            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*package*/ void cancelAvailability(){
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*package*/ static class EventSenderThread extends Thread {

        private static final int
                /**
                 * Amount of time in millis to wait until cancelling the polling of the next event
                 */
                MAXIMUM_WAIT_TIME = 1000;

        private static final LinkedBlockingQueue<Messenger>
                MESSENGER_QUEUE = new LinkedBlockingQueue<>();

        private static boolean
                activeSenderThread = false;

        private EventSenderThread(){
            start();
        }

        @Override
        public void run() {
            if (activeSenderThread) return;
            activeSenderThread = true;
            Log.i("ESThread", "started");

            Messenger messenger = null;
            byte[]data;
            while (hasConnections()) {
                try {
                    messenger = MESSENGER_QUEUE.poll(MAXIMUM_WAIT_TIME, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (messenger != null) {
                    try {
                        data = messenger.toString().getBytes(TEXT_ENCODING);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        continue;
                    }
                    for (String address:messenger.getReceptors()){
                        if (connections.containsKey(address))
                            connections.get(address).send(data);
                    }
                }
            }

            activeSenderThread = false;
        }

        /*package*/ static void send(Messenger messenger) {
            if (!MESSENGER_QUEUE.offer(messenger)) {
                Log.w("ESThread", "Couldn't add Messenger.");
            }
        }
    }

    private static class EventReceiverThread extends Thread {

        private static final int
                SLEEP_TIME = 100;

        private static boolean
                activeReceiverThread = false;

        private EventReceiverThread(){
            start();
        }

        @Override
        public void run(){
            if (activeReceiverThread)return;
            activeReceiverThread = true;

            Connection connection;
            boolean received;
            while(hasConnections()) {
                received = false;
                for (String address:connections.keySet()) {
                    connection = connections.get(address);
                    if (connection == null)continue;
                    for (Messenger messenger :connection.readEvents()){
                        received = true;
                        messenger.onReception();
                    }
                }
                if (!received) try {
                    sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            activeReceiverThread = false;
        }
    }
}
