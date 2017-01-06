package mobile.data.usage.spyspyyou.layouttesting.game.events;

import android.app.Activity;
import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Event;
import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.GameActivity;

public abstract class GameEvent extends Event {

    protected static String[]receptors = {};

    protected GameEvent() {
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

    public abstract void apply(Game game);

    public static void setReceptors(String[] mReceptors){
        receptors = mReceptors;
    }

    @Override
    public void onEventSendFailure(String[] addresses) {
        Log.i("GameEvent", "failed to send event: " + toString());
    }
}
