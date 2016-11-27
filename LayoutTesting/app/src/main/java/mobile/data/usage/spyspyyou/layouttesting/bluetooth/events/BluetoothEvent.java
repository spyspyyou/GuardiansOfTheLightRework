package mobile.data.usage.spyspyyou.layouttesting.bluetooth.events;

import android.support.annotation.Nullable;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Connection;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Event;

public abstract class BluetoothEvent extends Event {

    public BluetoothEvent(Connection[] receptors) {
        super(receptors);
    }

    public BluetoothEvent(String eventString){
        super(eventString);
    }

    @Nullable
    public static BluetoothEvent fromEventString(String eventString){
        switch(eventString.charAt(1)){
            default: return null;
        }
    }

    @Override
    public String toString() {
        return "B" + super.toString();
    }
}
