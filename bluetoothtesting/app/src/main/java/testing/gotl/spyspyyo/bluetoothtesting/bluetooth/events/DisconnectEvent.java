package testing.gotl.spyspyyo.bluetoothtesting.bluetooth.events;

import android.content.Intent;

import testing.gotl.spyspyyo.bluetoothtesting.UI.activities.MainActivity;

public class DisconnectEvent extends BluetoothEvent {

    public DisconnectEvent(String[] addresses) {
        super(addresses);
    }

    public DisconnectEvent(String eventString) {
        super(eventString);
    }

    @Override
    public String toString() {
        return super.toString() + 'D';
    }

    @Override
    public void handle() {

    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }
}
