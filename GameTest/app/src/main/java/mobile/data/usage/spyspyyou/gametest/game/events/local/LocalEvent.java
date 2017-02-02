package mobile.data.usage.spyspyyou.gametest.game.events.local;

import mobile.data.usage.spyspyyou.gametest.game.events.GameEvent;

public abstract class LocalEvent extends GameEvent {

    @Override
    public void send() {
        onReception();
    }
}
