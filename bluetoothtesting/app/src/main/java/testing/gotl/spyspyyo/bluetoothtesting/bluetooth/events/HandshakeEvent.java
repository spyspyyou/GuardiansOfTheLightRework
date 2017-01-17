package testing.gotl.spyspyyo.bluetoothtesting.bluetooth.events;

import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.Connection;


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
