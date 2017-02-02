package mobile.data.usage.spyspyyou.gametest.game.events;

import java.util.Collection;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.teststuff.bluetooth.Messenger;

public abstract class GameEvent extends Messenger {

    protected static String[]GAME_RECEPTORS = {};

    protected GameEvent() {
        super(GAME_RECEPTORS);
    }

    public GameEvent(String eventString) throws InvalidMessageException {
        super(eventString);
    }

    @Override
    public void onReception() {
        Game.addEvent(this);
    }

    public abstract void apply(Game game);

    //todo:when the game starts this needs to be set by the ui section
    public static void setReceptors(Collection<String> mReceptors){
        GAME_RECEPTORS = mReceptors.toArray(GAME_RECEPTORS);
    }
}
