package mobile.data.usage.spyspyyou.gametest.game.events.local;

import mobile.data.usage.spyspyyou.layouttesting.game.events.GameEvent;

public abstract class LocalEvent extends GameEvent {
    @Override
    public void send() {
        handle();
    }
}
