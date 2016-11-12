package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;


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
        return null;
    }

    public void send(){
        EventSenderThread.send(this);
    }

    public Connection[] getReceptors(){
        return RECEPTORS;
    }
}
