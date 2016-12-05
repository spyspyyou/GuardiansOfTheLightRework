package mobile.data.usage.spyspyyou.layouttesting.ui.ui_events;

import android.app.Activity;
import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyHostActivity;

public class JoinSuccessfulEvent extends UIEvent {

    public JoinSuccessfulEvent(String[] receptors) {
        super(receptors);
    }

    /*package*/ JoinSuccessfulEvent(String eventString) {
        super(eventString);
    }

    @Override
    public String toString() {
        return super.toString() + 'S';
    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }

    @Override
    public void handle() {
        Log.i("JSEvent", "handling");
        Activity activity = App.accessActiveActivity(null);
        if (activity instanceof LobbyHostActivity){
            ((LobbyHostActivity)(activity)).updateListViews();
        }else {
            Log.i("TCEvent", "not in LobbyClientActivity");
        }
    }
}
