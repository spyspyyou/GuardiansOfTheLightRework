package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;


import android.util.Log;

public abstract class Event {

    protected static final char DATA_SEPERATION_CHAR = '\n';

    //MAC-ADDRESS(es) of the target device(s)
    private String[] receptors;

    public Event(String[] receptors){
        this.receptors = receptors;
    }

    public Event(String eventString){
        receptors = null;
    }

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
            default:
                Log.w("Event", "could not read Event from String" + eventString);
                throw new InvalidEventStringException();
        }
    }

    /*package*/ static class InvalidEventStringException extends Exception{

    }
}
