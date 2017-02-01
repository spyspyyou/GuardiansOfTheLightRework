package mobile.data.usage.spyspyyou.gametest.game.events.global;

import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.game.events.GameEvent;

public abstract class GlobalEvent extends GameEvent {

    protected GlobalEvent() {

    }

    protected GlobalEvent(String eventString) {
        super(eventString);
        if (Game.isHost())super.send();
    }

    @Override
    public void send() {
        super.send();
        handle();
    }

}

