package mobile.data.usage.spyspyyou.layouttesting.ui.ui_events;

import android.app.Activity;

import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyHostActivity;

public class LobbyLeftEvent extends UIEvent {

    public LobbyLeftEvent(String[] receptors) {
        super(receptors);
    }

    /*package*/ LobbyLeftEvent(String eventString) {
        super(eventString);
    }

    @Override
    public String toString() {
        return super.toString() + 'L';
    }

    @Override
    public void handle() {
        Activity activity = App.accessActiveActivity(null);
        if (activity instanceof LobbyHostActivity){
            ((LobbyHostActivity) activity).onPlayerLeft(SENDER_ADDRESS);
            //todo:snackBar with info
        }
    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }
}
