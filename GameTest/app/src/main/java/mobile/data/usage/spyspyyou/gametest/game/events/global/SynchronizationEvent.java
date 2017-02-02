package mobile.data.usage.spyspyyou.gametest.game.events.global;


import mobile.data.usage.spyspyyou.gametest.game.Game;

public class SynchronizationEvent extends GlobalEvent {

    private static final String KEY_ST = "st";

    private final int SYNC_TICK;

    public SynchronizationEvent(String dataString) throws InvalidMessageException {
        super(dataString);
        SYNC_TICK = getInt(KEY_ST);
    }

    public SynchronizationEvent(int tick){
        SYNC_TICK = tick;
    }

    @Override
    public void send() {
        putObject(KEY_ST, SYNC_TICK);
        super.send();
    }

    @Override
    public void apply(Game game) {
        game.setSynchronizedTick(SYNC_TICK);
    }
}
