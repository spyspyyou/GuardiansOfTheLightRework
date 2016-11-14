package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import testing.gotl.spyspyyo.bluetoothtesting.global.TODS;

public class Connection implements TODS {
    private static final short MAX_EVENTS_PER_READ_EVENTS_CALL = 5;
    private final byte INDEX;
    private final InputStream INPUT_STREAM;
    private final OutputStream OUTPUT_STREAM;
    private final BluetoothSocket BLUETOOTH_SOCKET;
    private boolean active = true;

    public Connection(BluetoothSocket pBluetoothSocket, byte index){
        BLUETOOTH_SOCKET = pBluetoothSocket;
        INDEX = index;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        if (BLUETOOTH_SOCKET == null)active = false;
        else {
            try {
                inputStream = pBluetoothSocket.getInputStream();
                outputStream = pBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e("Connection", "failed to get data streams");
                active = false;
                e.printStackTrace();
            }
        }
        INPUT_STREAM = inputStream;
        OUTPUT_STREAM = outputStream;
    }

    /**
     * this method reads from the connection's InputStream until it gets at most MAX_EVENTS_PER_READ_EVENTS_CALL Events or the stream has no more data
     * @return events - a list of Events from the InputStream
     */
    public ArrayList<Event> readEvents(){
        ArrayList<Event> events = new ArrayList<>();
        String eventString = "";
        char nextChar;
        int numberOfEventsRead = 0;
        try {
            while ((INPUT_STREAM.available() > 0 || eventString != "")
                    && numberOfEventsRead < MAX_EVENTS_PER_READ_EVENTS_CALL) {
                nextChar = (char) INPUT_STREAM.read();
                if (nextChar == EVENT_STRING_FINAL_CHAR){
                    Log.i("Connection", "Event received: " + eventString);
                    Event event = Event.fromEventString(eventString);
                    if (event!=null)events.add(event);
                    else Log.w("Connection", "Received an invalid Event: " + eventString);
                    eventString = "";
                    ++numberOfEventsRead;
                }else eventString += nextChar;
            }
        }catch (IOException e){
            //todo: handle the broken connection;
            active = false;
            e.printStackTrace();
        }
        return events;
    }

    public void disconnect(){
        active = false;
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
        ConnectionManager.getConnections()[INDEX] = null;
    }

    public BluetoothDevice getRemoteDevice(){
        return BLUETOOTH_SOCKET.getRemoteDevice();
    }

    public boolean isActive(){
        return active;
    }
}
