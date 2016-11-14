package testing.gotl.spyspyyo.bluetoothtesting.bluetooth.events;

import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.Connection;


public class HandshakeEvent extends BluetoothEvent {

    private final long IDENTIFIER;

    public HandshakeEvent(Connection[] receptors, long identifier) {
        super(receptors);
        IDENTIFIER = identifier;
    }

    @Override
    public void handle() {

    }

    @Override
    public void apply() {
        //not used here
    }

    @Override
    public void onEventSendFailure(Connection[] connections) {

    }
}
