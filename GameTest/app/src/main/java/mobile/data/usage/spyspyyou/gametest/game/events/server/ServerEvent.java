package mobile.data.usage.spyspyyou.gametest.game.events.server;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.events.GameEvent;

//an event sent and meant only to and for the server
public abstract class ServerEvent extends GameEvent {

    @Override
    public void send() {
        if (!Game.HOST)super.send();
        else onReception();
    }
}
