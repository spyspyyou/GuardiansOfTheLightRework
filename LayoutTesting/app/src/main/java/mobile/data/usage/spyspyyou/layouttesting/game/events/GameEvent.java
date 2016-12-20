package mobile.data.usage.spyspyyou.layouttesting.game.events;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Event;

public abstract class GameEvent extends Event {

    public GameEvent(String[] receptors) {
        super(receptors);
    }

    public GameEvent(String eventString) {
        super(eventString);
    }

    @Override
    public void handle() {

    }

    public abstract void apply();

    @Override
    public void onEventSendFailure(String[] addresses) {

    }
}
