package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;


import android.util.Log;

import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.events.BluetoothEvent;

public abstract class Event {

    protected static final char INFO_SEPARATION_CHAR = '\n';

    //MAC-ADDRESS(es) of the target device(s)
    private String[] receptors;

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

    public abstract String toString();

    public void send(){
        if (receptors == null){
            Log.w("Event", "no receptors. Trying to sending a received Event?");
            return;
        }
        ConnectionManager.EventSenderThread.send(this);
    }

    /*package*/ String[] getReceptors(){
        return receptors;
    }
    
    /*package*/ static Event fromEventString(String eventString) throws InvalidEventStringException {
        switch(eventString.charAt(0)){
            case 'B':return BluetoothEvent.fromEventString(eventString);
            default:
                Log.w("Event", "could not read Event from String" + eventString);
                throw new InvalidEventStringException();
        }
    }

    public static class InvalidEventStringException extends Exception{

    }
}
