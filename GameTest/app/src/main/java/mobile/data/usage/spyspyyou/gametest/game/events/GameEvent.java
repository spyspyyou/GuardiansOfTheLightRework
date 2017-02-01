package mobile.data.usage.spyspyyou.gametest.game.events;

import android.app.Activity;
import android.util.Log;

import java.util.Collection;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.teststuff.bluetooth.Messenger;
import mobile.data.usage.spyspyyou.gametest.ui.GameActivity;

public abstract class GameEvent extends Messenger {

    protected static String[]receptors = {};

    protected GameEvent() {
        super(receptors);
    }

    public GameEvent(String eventString) {
        super(eventString);
    }

    @Override
    public void onReception() {
        Game.addEvent(this);
    }

    public abstract void apply(Game game);

    //todo:when the game starts this needs to be set by the ui section
    public static void setReceptors(Collection<String> mReceptors){
        receptors = mReceptors;
    }

    @Override
    public void onEventSendFailure(String[] addresses) {
        Log.i("GameEvent", "failed to send event: " + toString());
    }
}
