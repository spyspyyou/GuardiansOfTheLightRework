package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.events.DisconnectEvent;
import testing.gotl.spyspyyo.bluetoothtesting.teststuff.TODS;

/*package*/ class Connection implements TODS {

    private static final char DATA_BLOCK_END_CHAR = '|';
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
    /*package*/ ArrayList<Event> readEvents(){
        ArrayList<Event> events = new ArrayList<>();
        String eventString = "";
        char nextChar;
        int numberOfEventsRead = 0;
        try {
            while ((BUFFERED_READER.ready() || !eventString.equals(""))
                    && numberOfEventsRead < MAX_EVENTS_PER_CALL) {
                nextChar = (char) BUFFERED_READER.read();
                if (nextChar == DATA_BLOCK_END_CHAR){
                    Log.i("Connection", "Event received: " + eventString);
                    try{
                        events.add(Event.fromEventString(eventString));
                    }catch (Event.InvalidEventStringException e){
                        continue;
                    }
                    eventString = "";
                    ++numberOfEventsRead;
                }else eventString += nextChar;
            }
        }catch (IOException e){
            close();
            e.printStackTrace();
        }
        return events;
    }

    /*package*/ synchronized void send(byte[]data) {
        try {
            OUTPUT_STREAM.write(data);
            OUTPUT_STREAM.write(DATA_BLOCK_END_CHAR);
        } catch (IOException e) {
            Log.w("Connection", "failed sending data, connection broken");
            close();
            e.printStackTrace();
        }
    }

    /*package*/ void close(){
        Log.i("Connection", "disconnecting " + BLUETOOTH_SOCKET.getRemoteDevice().getName());

        try {
            send(new DisconnectEvent(new String[]{getAddress()}).toString().getBytes(TEXT_ENCODING));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
    }

    /*package*/ String getAddress(){
        return BLUETOOTH_SOCKET.getRemoteDevice().getAddress();
    }
}
