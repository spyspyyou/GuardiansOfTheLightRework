package mobile.data.usage.spyspyyou.layouttesting.bluetooth;

import android.util.Log;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.events.BluetoothEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.UIEvent;

public abstract class Event {

    private static final char ADDRESS_StOP_INDICATOR = '\n';
    private final Connection[] RECEPTORS;
    protected final String SENDER_ADDRESS;

    public Event(Connection[] receptors){
        RECEPTORS = receptors;
        SENDER_ADDRESS = AppBluetoothManager.getAddress();
    }

    public Event(String[] receptors){
        ArrayList<Connection> receps = new ArrayList<>();
        for (String address:receptors){
            Connection connection = ConnectionManager.getConnectionToAddress(address);
            if (connection != null) receps.add(connection);
            else Log.w("Event", "Can't send to: " + address + ", device not connected");
        }
        RECEPTORS = (Connection[]) receps.toArray();
        SENDER_ADDRESS = AppBluetoothManager.getAddress();
    }

    public Event(String eventString){
        RECEPTORS = null;
        SENDER_ADDRESS = eventString.substring(1, eventString.indexOf(ADDRESS_StOP_INDICATOR));
    }


    /**
     * has to be done quickly! otherwise it will block further reception
     */
    public abstract void handle();

    public String toString(){
        return SENDER_ADDRESS + ADDRESS_StOP_INDICATOR;
    }

    /*package*/ void onEventSendFailure(Connection connection){
        onEventSendFailure(new Connection[]{connection});
    }

    public abstract void onEventSendFailure(Connection[] connections);

    /*package*/ static Event fromEventString(String eventString){
        eventString = eventString.substring(eventString.indexOf(ADDRESS_StOP_INDICATOR)+1);
        switch(eventString.charAt(0)){
            case 'B':
                return BluetoothEvent.fromEventString(eventString);
            case 'U':
                return UIEvent.fromEventString(eventString);
            default: return null;
        }
    }

    public void send(){
        if (RECEPTORS == null){
            Log.w("Event", "tried sending a received Event");
            return;
        }
        EventSenderThread.send(this);
    }

    /*package*/ Connection[] getReceptors(){
        return RECEPTORS;
    }
}
