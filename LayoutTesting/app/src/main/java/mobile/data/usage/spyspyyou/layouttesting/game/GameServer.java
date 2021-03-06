package mobile.data.usage.spyspyyou.layouttesting.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;

import mobile.data.usage.spyspyyou.layouttesting.game.events.global.SynchronizationEvent;

public class GameServer extends Game implements Tick{

    // how many ticks to wait until next send
    //the number is the number of seconds
    private static final int TICK_SEND_RATE = 2 * TICK;

    private int counter = 0;

    public GameServer(Resources resources, View rootView, Bitmap pixelMap, byte controlType) {
        super(resources, rootView, pixelMap, controlType, new String[]{});
        host = true;
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
