package testing.gotl.spyspyyo.bluetoothtesting.bluetooth.events;

import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.Connection;
import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.Event;

public abstract class BluetoothEvent extends Event {

    public BluetoothEvent(Connection[] receptors) {
        super(receptors);
    }

    public static BluetoothEvent fromEventString(String eventString){
        switch(eventString.charAt(1)){
            default: return null;
        }
    }

    @Override
    public String toString() {
        return "B";
    }
}
