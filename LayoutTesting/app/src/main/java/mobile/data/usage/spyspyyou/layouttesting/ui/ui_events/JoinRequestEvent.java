package mobile.data.usage.spyspyyou.layouttesting.ui.ui_events;

import android.app.Activity;

import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyHostActivity;

public class JoinRequestEvent extends UIEvent{


    public JoinRequestEvent(String[] receptors) {
        super(receptors);
    }

    /*package*/ JoinRequestEvent(String eventString) {
        super(eventString);
    }

    @Override
    public String toString() {
        return super.toString() + 'J';
    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }

    @Override
    public void handle() {
        Activity activity = App.accessActiveActivity(null);
        if (activity instanceof LobbyHostActivity){
            ((LobbyHostActivity)(activity)).requestEntrance(SENDER_ADDRESS);
        }
    }

}
