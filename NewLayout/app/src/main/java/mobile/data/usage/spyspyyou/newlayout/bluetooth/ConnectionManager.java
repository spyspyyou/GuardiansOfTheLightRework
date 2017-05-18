package mobile.data.usage.spyspyyou.newlayout.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static android.util.Log.i;

/*package*/ class ConnectionManager {

    /**
     * should there be more than 10, the UUID generation has to be altered
     */
    private static final byte MAX_CONNECTIONS = 7;

    private static final int
            // in  millis
            TIME_OUT_LONG = 5000,
            //in nanos
            TIME_OUT_SHORT = 500;

    private static final String BASE_UUID_STRING = "fc165dae-c277-4854-be70-b38d0486e35";
    private static final UUID[] UUID_ARRAY = new UUID[MAX_CONNECTIONS];
    static {
        for (int i = 0; i < MAX_CONNECTIONS; ++i){
            UUID_ARRAY[i] = UUID.fromString(BASE_UUID_STRING + i);
        }
    }

    private static final Map<BluetoothDevice, Connection> connections = new LinkedHashMap<>();
    private volatile Queue<UUID> freeUUIDs = new Qu<>();

    private static ACThread aCThread;
    private static CCThread cCThread;

    /*package*/ static void releaseAll(){
        stopServer();
        stopClient();
        disconnect();
    }

    /*package*/ static void startServer(){
        if (aCThread != null){
            Log.i("ConnectionManager", "startServer, server already running");
            return;
        }
        stopClient();
        disconnect();
        aCThread = new ACThread();
        aCThread.start();
        Log.i("ConnectionManager", "started the Server");
    }

    /*package*/ static void stopServer(){
        aCThread.cancelAvailability();
        aCThread = null;
        i("ConnectionManager", "stopped the Server");
    }

    /*package*/ static void startClient(){
        if (cCThread != null){
            Log.i("ConnectionManager", "startClient, client already running");
            return;
        }
        stopServer();
        disconnect();
        cCThread = new CCThread();
        cCThread.start();
        Log.i("ConnectionManager", "started the Server");
    }

    /*package*/ static void stopClient(){
        cCThread.cancel();
        cCThread = null;
        i("ConnectionManager", "stopped the Server");
    }

    /*package*/ static void connect(BluetoothDevice bluetoothDevice, @Nullable AppBluetoothManager.ConnectionListener listener){
        if (cCThread != null) {
            cCThread.connectionQueue.add(new ConnectionRequest(bluetoothDevice, listener));
            Log.d("ConnectionManager", "added " + bluetoothDevice.getName() + " to the connection list");
        }else
            Log.d("ConnectionManager", "ccThread not running");
    }

    /*package*/ static void disconnect(BluetoothDevice device) {
        if (connections.containsKey(device)){
            connections.get(device).close();
            connections.remove(device);
        } else {
            Log.w("ConnectionManager", "No connection to device trying to disconnect from.");
        }
    }

    /*package*/ static void disconnect(){
        clearConnectionQueue();
        for (BluetoothDevice bluetoothDevice:connections.keySet()){
            connections.get(bluetoothDevice).close();
        }
        connections.clear();
        i("ConnectionManager", "disconnected all");
    }

    /*package*/ static void send(BluetoothDevice[]receptors, Message message) {
        for (BluetoothDevice receptor:receptors){
            if (connections.containsKey(receptor))connections.get(receptor).send(message);
        }
    }

    /*package*/ static void addListener(BluetoothDevice bluetoothDevice, AppBluetoothManager.ConnectionListener listener){
        Connection connection = connections.get(bluetoothDevice);
        if (connection != null){
            connection.LISTENER.add(listener);
        }
    }

    /*package*/ static void removeListener(BluetoothDevice bluetoothDevice, AppBluetoothManager.ConnectionListener listener){
        Connection connection = connections.get(bluetoothDevice);
        if (connection != null){
            connection.LISTENER.remove(listener);
        }
    }

    /*package*/ static boolean isConnectionQueueEmpty(){
        return cCThread.connectionQueue.size() == 0;
    }

    private static boolean hasConnections(){
        return !connections.isEmpty();
    }

    private static class CCThread extends Thread {

        private LinkedBlockingQueue<ConnectionRequest> connectionQueue = new LinkedBlockingQueue<>();

        BluetoothSocket bluetoothSocket = null;

        @Override
        public void run() {
            Log.d("CCThread", "Started CCThread");

            ConnectionRequest element;
            while (!isInterrupted()){
                try {
                    element = connectionQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

                if (connections.containsKey(element.BLUETOOTH_DEVICE.getAddress())) {
                    Log.w("CCThread", "Already connected to device\nName: " + element.BLUETOOTH_DEVICE.getName() + "\nAddress: " + element.BLUETOOTH_DEVICE.getAddress());
                    continue;
                }

                Log.d("CCThread", "Starting connection process\nName: " + element.BLUETOOTH_DEVICE.getName() + "\nAddress: " + element.BLUETOOTH_DEVICE.getAddress());
                //todo: only check free UUIDs
                for (byte index = 0; index < UUID_ARRAY.length && !isInterrupted(); ++index) {
                    Log.d("CCThread", "Attempt with uuid index: " + index);
                    try {
                        bluetoothSocket = element.BLUETOOTH_DEVICE.createRfcommSocketToServiceRecord(UUID_ARRAY[index]);
                        Log.d("CCThread", "received BluetoothSocket");
                    } catch (IOException e) {
                        Log.e("CCThread", "Failed to create BluetoothSocket for uuid index: " + index);
                        e.printStackTrace();
                        continue;
                    }

                    try {
                        Log.d("CCThread", "starting connection");
                        bluetoothSocket.connect();
                        Log.d("CCThread", "successful connect");
                        new Connection(bluetoothSocket, UUID_ARRAY[index], element.LISTENER);
                        Log.d("CCThread", "created Connection");
                        break;
                    } catch (IOException e) {
                        Log.w("CCThread", "Failed to connect using uuid with index " + index);
                        try {bluetoothSocket.close();} catch (IOException e1) {e1.printStackTrace();}
                        bluetoothSocket = null;
                        e.printStackTrace();
                    }
                }

                if (bluetoothSocket == null) {
                    Log.e("CCThread", "Failed connecting\nName: " + element.BLUETOOTH_DEVICE.getName() + "\nAddress: " + element.BLUETOOTH_DEVICE.getAddress());
                    if (element.LISTENER != null)element.LISTENER.onConnectionFailed(element.BLUETOOTH_DEVICE);
                }
            }

            Log.d("CCThread", "Stopped CCThread");
        }

        private void cancel() {
            interrupt();
            connectionQueue.clear();
            try {
                if (bluetoothSocket != null)bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static class ConnectionRequest {

        private final BluetoothDevice BLUETOOTH_DEVICE;
        private final AppBluetoothManager.ConnectionListener LISTENER;

        private ConnectionRequest(BluetoothDevice bluetoothDevice, @Nullable AppBluetoothManager.ConnectionListener listener){
            BLUETOOTH_DEVICE = bluetoothDevice;
            LISTENER = listener;
        }
    }

    private static class ACThread extends Thread {

        private ACThread() {
            for (UUID uuid:UUID_ARRAY){
                try {
                    bluetoothServerSockets.put(uuid, AppBluetoothManager.getBluetoothServerSocket(uuid));
                    freeUUIDS.add(uuid);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ACThread", "Failed to create ServerSocket for UUID: " + uuid);
                }
            }
            Log.d("ACThread", "Created " + bluetoothServerSockets.size() + " server sockets");
        }

        @Override
        public void run() {
            Log.d("ACThread", "Started ACThread");
            BluetoothServerSocket bluetoothServerSocket;
            BluetoothSocket bluetoothSocket;
            UUID uuid;
            while(serverActive) {
                if (freeUUIDS.isEmpty())cancelAvailability();
                uuid = freeUUIDS.get(0);
                try {
                    bluetoothServerSocket = AppBluetoothManager.getBluetoothServerSocket(uuid);
                    if (bluetoothServerSocket != null) {
                        Log.d("ACThread", "waiting for uuid " + uuid);
                        bluetoothSocket = bluetoothServerSocket.accept();
                        Log.d("ACThread", "accepted connection");
                        new Connection(bluetoothSocket, uuid, null);
                    }
                } catch (IOException ignored) {
                    Log.d("ACThread", "Failed accepting connection with uuid " + uuid);
                }
            }
            Log.d("ACThread", "Stopped ACThread");
        }

        /*package*/ void cancelAvailability() {
            Log.d("ACThread", "Canceling ACThread");
            serverActive = false;
            freeUUIDS.clear();
            for (UUID key : bluetoothServerSockets.keySet()) {
                try {
                    bluetoothServerSockets.get(key).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bluetoothServerSockets.clear();
        }
    }

    private static class Connection {
        private final UUID UUID;

        private final ArrayList<AppBluetoothManager.ConnectionListener> LISTENER = new ArrayList<>();
        private final LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

        private final ObjectInputStream INPUT_STREAM;
        private final ObjectOutputStream OUTPUT_STREAM;
        private final BluetoothSocket BLUETOOTH_SOCKET;

        private final Handshake HANDSHAKE = new Handshake();

        private Connection(BluetoothSocket bluetoothSocket, UUID uuid, @Nullable AppBluetoothManager.ConnectionListener listener) throws IOException {
            BLUETOOTH_SOCKET = bluetoothSocket;
            UUID = uuid;

            try {
                OUTPUT_STREAM = new ObjectOutputStream(bluetoothSocket.getOutputStream());
                OUTPUT_STREAM.writeObject(HANDSHAKE);
                OUTPUT_STREAM.flush();
                Log.d("Connection", "Got the ObjectStream out");
                INPUT_STREAM = new ObjectInputStream(bluetoothSocket.getInputStream());
                Log.d("Connection", "Got the ObjectStream in");
            }catch (Exception e){
                BLUETOOTH_SOCKET.close();
                e.printStackTrace();
                throw new IOException("Failed to obtain the object streams");
            }

            Log.d("Connection", "successfully got streams, adding/handling Connection");
            //adding the connection, assuring handling threads active
            connections.put(getAddress(), this);
            if (serverActive)aCThread.freeUUIDS.remove(UUID);
            if (!ConnectionHandlerThread.activeHandler)new ConnectionHandlerThread().start();

            Log.d("Connection", "notifying LISTENER");
            AppBluetoothManager.notifyConnectionEstablished(BLUETOOTH_SOCKET.getRemoteDevice());
            if (listener != null) {
                LISTENER.add(listener);
                listener.onConnectionEstablished(BLUETOOTH_SOCKET.getRemoteDevice());
            }
            i("Connection", "successfully established a connection to\nName: " + BLUETOOTH_SOCKET.getRemoteDevice().getName() + "\nAddress: " + getAddress());
        }

        private void performReadWrite() {
            try {
                Message message = messageQueue.poll(TIME_OUT_SHORT, TimeUnit.NANOSECONDS);
                if (message == null) message = HANDSHAKE;
                Log.v("Connection", "sending message of type " + message.getClass().getSimpleName());
                OUTPUT_STREAM.writeObject(message);
                OUTPUT_STREAM.flush();
                Log.v("Connection", "finished writing message");
                Log.v("Connection", "reading message");
                message = ((Message) INPUT_STREAM.readObject());
                if (message instanceof Handshake)Log.v("Connection", "read message of type Handshake");
                else Log.d("Connection", "read message of type " + message.getClass().getSimpleName());
                message.onReception();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Connection", "failed read/write, connection broken");
                close();
            } catch (Exception e) {
                Log.w("Connection", "Received invalid Message");
                e.printStackTrace();
            }
        }

        /*package*/ synchronized void send(Message message) {
            if (!messageQueue.offer(message)) Log.w("Connection", "Message Queue full for Connection to " + BLUETOOTH_SOCKET.getRemoteDevice().getName());
            else i("Connection", "added message of type " + message.getClass().getSimpleName());
        }

        /*package*/ void close(){
            Log.w("Connection", "Closing connection\nName: " + BLUETOOTH_SOCKET.getRemoteDevice().getName() + "\nAddress: " + getAddress());

            for (AppBluetoothManager.ConnectionListener listener : LISTENER)
                listener.onConnectionClosed(BLUETOOTH_SOCKET.getRemoteDevice());

            try {
                INPUT_STREAM.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                OUTPUT_STREAM.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BLUETOOTH_SOCKET.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (connections.containsKey(getAddress()))
                connections.remove(getAddress());
            if (serverActive && aCThread != null)aCThread.freeUUIDS.add(UUID);
        }

        /*package*/ String getAddress(){
            return BLUETOOTH_SOCKET.getRemoteDevice().getAddress();
        }
    }

    private static class ConnectionHandlerThread extends Thread {

        private static boolean activeHandler = false;

        @Override
        public void run() {
            Log.d("CHThread", "Started CHThread");
            Connection connection;
            while (hasConnections()){
                for (String key:new TreeSet<>(connections.keySet())){
                    connection = connections.get(key);
                    if (connection != null)connection.performReadWrite();
                }
            }

            Log.d("CHThread", "Stopped CHThread");
        }
    }
}
