package mobile.data.usage.spyspyyou.gametest.game;

import android.util.Log;
import android.view.View;

import mobile.data.usage.spyspyyou.gametest.game.entities.Gum;
import mobile.data.usage.spyspyyou.gametest.game.entities.Player;
import mobile.data.usage.spyspyyou.gametest.game.events.global.GumDeathEvent;
import mobile.data.usage.spyspyyou.gametest.game.events.global.SynchronizationEvent;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

public class GameServer extends Game implements Tick{

    // how many ticks to wait until next send
    //the number is the number of seconds
    private static final int TICK_SEND_RATE = 2 * TICK;

    private int counter = 0;

    public GameServer(View view) {
        super(view);
    }

    @Override
    protected void update() {
        super.update();
        for (int i = 0; i < gums.size(); ++i){
            if (!hitPlayer(gums.valueAt(i)))outOfBounds(gums.valueAt(i));
        }

        ++counter;
        if (counter >= TICK_SEND_RATE){
            new SynchronizationEvent(gameThread.getSynchronizedTick()).send();
            counter = 0;
        }
    }

    private void outOfBounds(Gum gum){
        Vector2D pos = gum.position;
        if (gameWorld.isImpassable(pos) || pos.x < 0 || pos.y < 0 || pos.x > gameWorld.size() || pos.y > gameWorld.size()){
            Log.i("Server", "gum out of bounds");
            new GumDeathEvent(gums.keyAt(gums.indexOfValue(gum))).send();
        }
    }

    private boolean hitPlayer(Gum gum){
        for (Player player:players.values()){
            if (player.isBlue() != gum.isBlue() && gum.position.distance(player.position) < player.getRadius()){
                //new PlayerShotEvent(player, gum).send();
                return  false;
            }
        }
        return false;
    }
}
