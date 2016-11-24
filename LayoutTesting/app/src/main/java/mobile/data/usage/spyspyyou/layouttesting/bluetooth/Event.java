package mobile.data.usage.spyspyyou.layouttesting.bluetooth;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.events.BluetoothEvent;

public abstract class Event {

    private final Connection[] RECEPTORS;

    public Event(Connection[] receptors){
        RECEPTORS = receptors;
    }

    /**
     * has to be done quickly! otherwise it will block further reception
     */
    public abstract void handle();
    public abstract void apply();
    public abstract String toString();
    public abstract void onEventSendFailure(Connection[] connections);

    public static Event fromEventString(String eventString){
        switch(eventString.charAt(0)){
            case 'B':
                return BluetoothEvent.fromEventString(eventString);
            default: return null;
        }
    }

    public void send(){
        EventSenderThread.send(this);
    }

    public Connection[] getReceptors(){
        return RECEPTORS;
    }
}
