package mobile.data.usage.spyspyyou.layouttesting.bluetooth.events;

import android.support.annotation.Nullable;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Event;

public abstract class BluetoothEvent extends Event {

    public BluetoothEvent(String[] addresses){
        super(addresses);
    }

    public BluetoothEvent(String eventString){
        super(eventString);
    }

    @Nullable
    public static BluetoothEvent fromEventString(String eventString){
        switch(eventString.charAt(eventString.indexOf(ADDRESS_STOP_INDICATOR)+1)){
            case 'H':return new HandshakeEvent(eventString);
            default: return null;
        }
    }

    @Override
    public String toString() {
        return "B" + super.toString();
    }
}
