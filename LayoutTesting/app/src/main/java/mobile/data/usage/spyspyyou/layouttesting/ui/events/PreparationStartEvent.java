package mobile.data.usage.spyspyyou.layouttesting.ui.events;

import android.app.Activity;
import android.content.Intent;

import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyClientActivity;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.PreparationActivity;

public class PreparationStartEvent extends UIEvent {

    public PreparationStartEvent(String[] receptors) {
        super(receptors);
    }

    public PreparationStartEvent(String eventString) {
        super(eventString);
    }

    @Override
    public String toString() {
        return super.toString() + 'P';
    }

    @Override
    public void handle() {
        Activity activity = App.accessActiveActivity(null);
        if (activity instanceof LobbyClientActivity){
            activity.startActivity(new Intent(activity.getBaseContext(), PreparationActivity.class));
        }
    }

    @Override
    public void onEventSendFailure(String[] addresses) {
        new ReturnToLobbyEvent(RECEPTORS);
    }
}
