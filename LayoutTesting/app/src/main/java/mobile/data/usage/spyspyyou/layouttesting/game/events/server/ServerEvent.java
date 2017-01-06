package mobile.data.usage.spyspyyou.layouttesting.game.events.server;

import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.game.events.GameEvent;

//an event sent and meant only to and for the server
public abstract class ServerEvent extends GameEvent {

    @Override
    public void send() {
        if (!Game.isHost())super.send();
        else handle();
    }
}
