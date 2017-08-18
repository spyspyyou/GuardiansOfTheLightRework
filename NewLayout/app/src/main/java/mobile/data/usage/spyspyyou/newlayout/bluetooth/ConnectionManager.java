package mobile.data.usage.spyspyyou.newlayout.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static android.util.Log.d;
import static android.util.Log.i;
import static android.util.Log.w;

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

    private static final Map<BluetoothDevice, Connection> connections = new LinkedHashMap<>();
    private static final ArrayList<UUID> freeUUIDs = new ArrayList<>();
    static {
        for (int i = 0; i < MAX_CONNECTIONS; ++i)freeUUIDs.add(UUID.fromString(BASE_UUID_STRING + i));
    }

    private static ACThread aCThread;
    private static CCThread cCThread;
    private static SHandler sHandler;

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
        aCThread.cancel();
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
            d("ConnectionManager", "added " + bluetoothDevice.getName() + " to the connection list");
        }else
            d("ConnectionManager", "ccThread not running");
    }

    /*package*/ static void disconnect(BluetoothDevice device) {
        if (connections.containsKey(device)){
            connections.get(device).close();
            connections.remove(device);
        } else {
            w("ConnectionManager", "No connection to device trying to disconnect from.");
        }
    }

    /*package*/ static void disconnect(){
        for (BluetoothDevice bluetoothDevice:connections.keySet())
            connections.get(bluetoothDevice).close();
        connections.clear();
        i("ConnectionManager", "disconnected all");
    }

    /*package*/ static void send(BluetoothDevice[]receptors, Message message) {
        for (BluetoothDevice receptor:receptors)
            if (connections.containsKey(receptor))connections.get(receptor).send(message);
    }

    /*package*/ static void addListener(BluetoothDevice bluetoothDevice, AppBluetoothManager.ConnectionListener listener){
        Connection connection = connections.get(bluetoothDevice);
        if (connection != null) connection.LISTENER.add(listener);
    }

    /*package*/ static void removeListener(BluetoothDevice bluetoothDevice, AppBluetoothManager.ConnectionListener listener){
        Connection connection = connections.get(bluetoothDevice);
        if (connection != null) connection.LISTENER.remove(listener);
    }

    /*package*/ static boolean isConnectionQueueEmpty(){
        return cCThread.connectionQueue.size() == 0;
    }

    private static boolean hasConnections(){
        return !connections.isEmpty();
    }

    private static class CCThread extends Thread {

        private LinkedBlockingQueue<ConnectionRequest> connectionQueue = new LinkedBlockingQueue<>();
        private BluetoothSocket bluetoothSocket = null;

        @Override
        public void run() {
            d("CCThread", "Started CCThread");
            ConnectionRequest element;
            Iterator<UUID> uuidIt;
            UUID uuid;

            while (!isInterrupted()){
                try {
                    element = connectionQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

                if (connections.containsKey(element.BLUETOOTH_DEVICE)) {
                    w("CCThread", "Already connected to device\nName: " + element.BLUETOOTH_DEVICE.getName() + "\nAddress: " + element.BLUETOOTH_DEVICE.getAddress());
                    continue;
                }

                d("CCThread", "Starting connection process\nName: " + element.BLUETOOTH_DEVICE.getName() + "\nAddress: " + element.BLUETOOTH_DEVICE.getAddress());
                uuidIt = freeUUIDs.iterator();
                if (!uuidIt.hasNext()) w("CCThread", "There are no free UUIDs left");
                while (uuidIt.hasNext() && !isInterrupted()) {
                    uuid = uuidIt.next();
                    d("CCThread", "Attempt with uuid: " + uuid);
                    try {
                        bluetoothSocket = element.BLUETOOTH_DEVICE.createRfcommSocketToServiceRecord(uuid);
                        d("CCThread", "received BluetoothSocket");
                    } catch (IOException e) {
                        Log.e("CCThread", "Failed to create BluetoothSocket for uuid: " + uuid);
                        bluetoothSocket = null;
                        e.printStackTrace();
                        continue;
                    }

                    try {
                        d("CCThread", "starting connection");
                        bluetoothSocket.connect();
                        d("CCThread", "successful connect");
                        new Connection(bluetoothSocket, uuid, element.LISTENER);
                        uuidIt.remove();
                        d("CCThread", "created Connection");
                        break;
                    } catch (IOException e) {
                        w("CCThread", "Failed to connect using uuid " + uuid);
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
            d("CCThread", "Stopped CCThread");
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

        private BluetoothServerSocket bluetoothServerSocket;

        @Override
        public void run() {
            d("ACThread", "Started ACThread");
            BluetoothSocket bluetoothSocket = null;
            Iterator<UUID> uuidIt = freeUUIDs.iterator();
            UUID uuid;

            while (!isInterrupted()) {
                if (!uuidIt.hasNext()) {
                    if (freeUUIDs.size() == 0) {
                        try {
                            freeUUIDs.wait();
                        } catch (InterruptedException e) {
                            break;
                        }
                    } else uuidIt = freeUUIDs.iterator();
                }
                uuid = uuidIt.next();

                try {
                    bluetoothServerSocket = AppBluetoothManager.getBluetoothServerSocket(uuid);
                } catch (IOException e) {
                    w("ACThread", "Failed to get BluetoothServerSocket with uuid: " + uuid);
                    continue;
                }

                if (bluetoothServerSocket != null) {
                    w("ACThread", "Received a null BluetoothServerSocket using uuid: " + uuid);
                    continue;
                }

                try {
                    d("ACThread", "waiting for uuid " + uuid);
                    bluetoothSocket = bluetoothServerSocket.accept(TIME_OUT_LONG);
                    bluetoothServerSocket.close();
                    d("ACThread", "accepted connection");
                    new Connection(bluetoothSocket, uuid, null);
                    uuidIt.remove();
                } catch (IOException e) {
                    try {bluetoothServerSocket.close();} catch (IOException ignored) {}
                    if (bluetoothSocket!=null) try {bluetoothSocket.close();} catch (IOException ignored1) {}
                    d("ACThread", "Failed accepting connection with uuid " + uuid);
                }
            }
            d("ACThread", "Stopped ACThread");
        }

        /*package*/
        synchronized void cancel() {
            d("ACThread", "Canceling ACThread");
            interrupt();
            try {bluetoothServerSocket.close();} catch (IOException ignored) {}
        }
    }

    private static class Connection {

        private final UUID UUID;
        private final ArrayList<AppBluetoothManager.ConnectionListener> LISTENER = new ArrayList<>();

        private final ObjectInputStream INPUT_STREAM;
        private final ObjectOutputStream OUTPUT_STREAM;
        private final BluetoothSocket BLUETOOTH_SOCKET;

        private RHandler rHandler;

        private Connection(BluetoothSocket bluetoothSocket, UUID uuid, @Nullable AppBluetoothManager.ConnectionListener listener) throws IOException {
            BLUETOOTH_SOCKET = bluetoothSocket;
            UUID = uuid;

            try {
                OUTPUT_STREAM = new ObjectOutputStream(bluetoothSocket.getOutputStream());
                OUTPUT_STREAM.writeObject(new Handshake());
                OUTPUT_STREAM.flush();
                d("Connection", "Got the ObjectStream out");
                INPUT_STREAM = new ObjectInputStream(bluetoothSocket.getInputStream());
                d("Connection", "Got the ObjectStream in");
            }catch (Exception e){
                try{BLUETOOTH_SOCKET.close();}catch(IOException ignored){}
                e.printStackTrace();
                throw new IOException("Failed to obtain the object streams");
            }

            d("Connection", "successfully got streams, adding/handling Connection");
            connections.put(BLUETOOTH_SOCKET.getRemoteDevice(), this);
            rHandler = new RHandler();
            rHandler.start();

            d("Connection", "notifying LISTENER");
            AppBluetoothManager.notifyConnectionEstablished(BLUETOOTH_SOCKET.getRemoteDevice());
            if (listener != null) {
                LISTENER.add(listener);
                listener.onConnectionEstablished(BLUETOOTH_SOCKET.getRemoteDevice());
            }
            i("Connection", "successfully established a connection to\nName: " + BLUETOOTH_SOCKET.getRemoteDevice().getName() + "\nAddress: " + getAddress());
        }

        /*package*/ synchronized void send(Message message) {
            if (!messageQueue.offer(message)) w("Connection", "Message Queue full for Connection to " + BLUETOOTH_SOCKET.getRemoteDevice().getName());
            else i("Connection", "added message of type " + message.getClass().getSimpleName());
        }

        /*package*/ void close(){
            w("Connection", "Closing connection\nName: " + BLUETOOTH_SOCKET.getRemoteDevice().getName() + "\nAddress: " + getAddress());
            rHandler.interrupt();

            for (AppBluetoothManager.ConnectionListener listener : LISTENER)
                listener.onConnectionClosed(BLUETOOTH_SOCKET.getRemoteDevice());

            try {INPUT_STREAM.close();} catch (IOException ignored) {}
            try {OUTPUT_STREAM.close();} catch (IOException ignored) {}
            try {BLUETOOTH_SOCKET.close();} catch (IOException ignored) {}

            if (connections.containsKey(BLUETOOTH_SOCKET.getRemoteDevice()))
                connections.remove(BLUETOOTH_SOCKET.getRemoteDevice());

            synchronized (freeUUIDs){
                freeUUIDs.add(UUID);
                freeUUIDs.notifyAll();
            }
        }

        /*package*/ String getAddress(){
            return BLUETOOTH_SOCKET.getRemoteDevice().getAddress();
        }
    }

    private static class SHandler extends Thread {

        private final LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            d("CHThread", "Started CHThread");
            try {
                Message message = messageQueue.poll();
                Log.v("Connection", "sending message of type " + message.getClass().getSimpleName());

                .writeObject(message);
                .flush();
                Log.v("Connection", "finished writing message");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Connection", "failed read/write, connection broken");
                close();
            } catch (InterruptedException ignored) {
                w("Connection", "Received invalid Message");
                InterruptedIOException
            }
            Connection connection;
            while (hasConnections()){
                for (String key:new TreeSet<>(connections.keySet())){
                    connection = connections.get(key);
                    if (connection != null)connection.performReadWrite();
                }
            }

            d("CHThread", "Stopped CHThread");
        }
    }

    private static class RHandler extends Thread{

        @Override
        public void run() {
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
                else d("Connection", "read message of type " + message.getClass().getSimpleName());
                message.onReception();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Connection", "failed read/write, connection broken");
                close();
            } catch (Exception e) {
                w("Connection", "Received invalid Message");
                e.printStackTrace();
            }
        }

    }
}
