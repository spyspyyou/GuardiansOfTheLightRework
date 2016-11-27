package mobile.data.usage.spyspyyou.layouttesting.ui.ui_events;

import android.support.annotation.Nullable;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Connection;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Event;

public abstract class UIEvent extends Event {

    public UIEvent(Connection[] receptors) {
        super(receptors);
    }

    public UIEvent(String[] receptors) {
        super(receptors);
    }

    public UIEvent(String eventString){
        super(eventString);
    }

    @Override
    public String toString() {
        return 'U' + super.toString();
    }

    @Nullable
    public static UIEvent fromEventString(String eventString){
        switch(eventString.charAt(1)){
            case 'A': return new GameInformationAnswerEvent(eventString);
            case 'R': return  new GameInformationRequestEvent(eventString);
            default: return null;
        }
    }
}
