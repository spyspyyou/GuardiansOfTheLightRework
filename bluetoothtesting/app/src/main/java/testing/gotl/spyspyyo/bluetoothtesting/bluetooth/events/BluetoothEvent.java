package testing.gotl.spyspyyo.bluetoothtesting.bluetooth.events;

import android.support.annotation.Nullable;

import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.Event;

public abstract class BluetoothEvent extends Event {

    public BluetoothEvent(String[] addresses){
        super(addresses);
    }

    public BluetoothEvent(String evenString){
        super(eventString);
    }

    @Nullable
    public static BluetoothEvent fromEventString(String eventString){
        switch(eventString.charAt(eventString.indexOf(INFO_SEPARATION_CHAR)+1)){
            case 'H':return new HandshakeEvent(eventString);
            default: return null;
        }
    }

    @Override
    public String toString() {
        return "B" + super.toString();
    }
}
