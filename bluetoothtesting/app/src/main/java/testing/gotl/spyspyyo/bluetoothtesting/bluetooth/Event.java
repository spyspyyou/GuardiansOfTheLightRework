package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;


import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.events.BluetoothEvent;

public abstract class Event {

    private final Connection[] RECEPTORS;

    public Event(Connection[] receptors){
        RECEPTORS = receptors;
    }

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
