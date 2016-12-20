package mobile.data.usage.spyspyyou.layouttesting.bluetooth;

import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.events.BluetoothEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.events.UIEvent;

public abstract class Event {

    protected static final char ADDRESS_STOP_INDICATOR = '\n';
    protected final String[] RECEPTORS;
    protected final String SENDER_ADDRESS;

    public Event(String[] receptors){
        RECEPTORS = receptors;
        SENDER_ADDRESS = AppBluetoothManager.getAddress();
    }

    public Event(String eventString){
        RECEPTORS = null;
        SENDER_ADDRESS = eventString.substring(1, eventString.indexOf(ADDRESS_STOP_INDICATOR));
    }

    /**
     * has to be done quickly! otherwise it will block further reception
     */
    public abstract void handle();

    public String toString(){
        return SENDER_ADDRESS + ADDRESS_STOP_INDICATOR;
    }

    public void onEventSendFailure(String addresses){
        onEventSendFailure(new String[]{addresses});
    }

    public abstract void onEventSendFailure(String[] addresses);

    /*package*/ static Event fromEventString(String eventString){
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

    /*package*/ String[] getReceptors(){
        return RECEPTORS;
    }
}
