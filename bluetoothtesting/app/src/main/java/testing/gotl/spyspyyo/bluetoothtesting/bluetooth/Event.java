package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;


import android.util.Log;

import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.events.BluetoothEvent;

public abstract class Event {

    protected static final char INFO_SEPARATION_CHAR = '\n';

    protected String[] receptors;
    protected final String SENDER_ADDRESS;

    public Event(String[] receptors){
        this.receptors = receptors;
        SENDER_ADDRESS = AppBluetoothManager.getLocalAddress();
    }

    public Event(String eventString){
        receptors = null;
        SENDER_ADDRESS = eventString.substring(1, eventString.indexOf(INFO_SEPARATION_CHAR));
    }

    /**
     * has to be done quickly! otherwise it will block further reception
     */
    public abstract void handle();

    public String toString(){
        return SENDER_ADDRESS + INFO_SEPARATION_CHAR;
    }

    /*package*/ static Event fromEventString(String eventString){
        switch(eventString.charAt(0)){
            case 'B':return BluetoothEvent.fromEventString(eventString);
            default:
                Log.w("Event", "could not read Event from String" + eventString);
                return null;
        }
    }

    public void send(){
        if (receptors == null){
            Log.w("Event", "no receptors. tried sending a received Event?");
            return;
        }
        EventSenderThread.send(this);
    }

    /*package*/ String[] getReceptors(){
        return receptors;
    }
}
