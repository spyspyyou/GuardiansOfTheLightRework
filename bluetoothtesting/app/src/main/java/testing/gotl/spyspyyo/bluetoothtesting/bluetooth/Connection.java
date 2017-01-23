package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import static testing.gotl.spyspyyo.bluetoothtesting.teststuff.TEST_VARIABLES.TEXT_ENCODING;

/*package*/ class Connection {

    private static final char DATA_BLOCK_END_CHAR = '\0';
    private static final short MAX_EVENTS_PER_CALL = 5;

    private final ArrayList<AppBluetoothManager.ConnectionListener> listeners = new ArrayList<>();

    private final InputStream INPUT_STREAM;
    private final OutputStream OUTPUT_STREAM;
    private final BluetoothSocket BLUETOOTH_SOCKET;
    private final BufferedReader BUFFERED_READER;

    /*package*/ Connection(BluetoothSocket bluetoothSocket, @Nullable AppBluetoothManager.ConnectionListener listener) throws IOException {
        BLUETOOTH_SOCKET = bluetoothSocket;

        try {
            INPUT_STREAM = bluetoothSocket.getInputStream();
            OUTPUT_STREAM = bluetoothSocket.getOutputStream();

            BUFFERED_READER = new BufferedReader(new InputStreamReader(INPUT_STREAM, TEXT_ENCODING));
        }catch (IOException e){
            BLUETOOTH_SOCKET.close();
            throw e;
        }

        ConnectionManager.addConnection(this);

        AppBluetoothManager.notifyConnectionEstablished(new Connection(bluetoothSocket, null).getAddress());
        if (listener != null) {
            listeners.add(listener);
            listener.onConnectionEstablished();
        }
        Log.i("Connection", "successfully established a connection to: " + BLUETOOTH_SOCKET.getRemoteDevice().getAddress());
    }

    /**
     * this method reads from the connection's InputStream until it gets at most MAX_EVENTS_PER_CALL Events or the stream has no more data
     * @return events - a list of Events from the InputStream
     */
    /*package*/ ArrayList<Messenger> readEvents(){
        ArrayList<Messenger> messengers = new ArrayList<>();
        String eventString = "";
        char nextChar;
        int count = 0;
        try {
            while ((BUFFERED_READER.ready() || !eventString.equals(""))
                    && count < MAX_EVENTS_PER_CALL) {
                nextChar = (char) BUFFERED_READER.read();
                if (nextChar == DATA_BLOCK_END_CHAR){
                    Log.i("Connection", "Messenger received: " + eventString);
                    try{
                        messengers.add(Messenger.fromMessageString(eventString));
                    }catch (Exception e){
                        e.printStackTrace();
                        continue;
                    }
                    eventString = "";
                    ++count;
                }else eventString += nextChar;
            }
        }catch (IOException e){
            close();
            e.printStackTrace();
        }
        return messengers;
    }

    /*package*/ synchronized void send(byte[]data) {
        try {
            OUTPUT_STREAM.write(data);
            OUTPUT_STREAM.write(DATA_BLOCK_END_CHAR);
            OUTPUT_STREAM.flush();
        } catch (IOException e) {
            Log.w("Connection", "failed sending data, connection broken");
            close();
            e.printStackTrace();
        }
    }

    /*package*/ void close(){
        Log.i("Connection", "closing connection " + BLUETOOTH_SOCKET.getRemoteDevice().getName());

        Iterator<AppBluetoothManager.ConnectionListener> i = listeners.iterator();
        while (i.hasNext()){
            i.next().onConnectionClosed();
        }

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

        ConnectionManager.removeClosedConnection(this);
    }

    /*package*/ String getAddress(){
        return BLUETOOTH_SOCKET.getRemoteDevice().getAddress();
    }
}
