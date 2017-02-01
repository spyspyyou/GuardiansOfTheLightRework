package mobile.data.usage.spyspyyou.gametest.game;

import mobile.data.usage.spyspyyou.gametest.game.events.global.SynchronizationEvent;
import mobile.data.usage.spyspyyou.gametest.utils.GameData;

public class GameServer extends Game implements Tick{

    // how many ticks to wait until next send
    //the number is the number of seconds
    private static final int TICK_SEND_RATE = 2 * TICK;

    private int counter = 0;

    public GameServer(GameData gameData) {
        super(gameData);
    }

    @Override
    protected void update() {
        super.update();
        ++counter;
        if (counter >= TICK_SEND_RATE){
            new SynchronizationEvent(gameThread.getSynchronizedTick()).send();
            counter = 0;
        }
    }
}
