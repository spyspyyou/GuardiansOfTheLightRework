package mobile.data.usage.spyspyyou.layouttesting.bluetooth.events;

public class HandshakeEvent extends BluetoothEvent {

    public HandshakeEvent(String[] addresses){
        super(addresses);
    }

    public HandshakeEvent(String eventString) {
        super(eventString);
    }

    @Override
    public void handle() {
        //Log.i("HandshakeEvent", "successful handshake with: " + SENDER_ADDRESS);
    }

    @Override
    public String toString() {
        return super.toString() + 'H';
    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }
}
