package mobile.data.usage.spyspyyou.layouttesting.game.events;

import android.app.Activity;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Event;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.GameActivity;

public abstract class GameEvent extends Event {

    public GameEvent(String[] receptors) {
        super(receptors);
    }

    public GameEvent(String eventString) {
        super(eventString);
    }

    @Override
    public void handle() {
        Activity activity = App.getCurrentActivity();
        if (activity instanceof GameActivity){
            ((GameActivity) activity).addEvent(this);
        }
    }

    public abstract void apply();

    @Override
    public void onEventSendFailure(String[] addresses) {

    }
}
