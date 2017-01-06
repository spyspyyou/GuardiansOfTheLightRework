package mobile.data.usage.spyspyyou.layouttesting.game.events.global;

import mobile.data.usage.spyspyyou.layouttesting.game.Game;

public class SynchronizationEvent extends GlobalEvent {

    private final int SYNC_TICK;

    public SynchronizationEvent(int tick){
        SYNC_TICK = tick;
    }

    @Override
    public void apply(Game game) {
        game.setSynchronizedTick(SYNC_TICK);
    }
}
