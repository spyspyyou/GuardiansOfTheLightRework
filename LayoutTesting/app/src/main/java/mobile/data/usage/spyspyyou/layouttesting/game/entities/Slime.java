package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.game.events.global.AddSlimeEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_SLIME;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.TICK;

public class Slime extends User {

    public Slime(Vector2D entityPosition, int size, SurfaceViewGame surfaceViewGame) {
        super(entityPosition, size, ID_SLIME, surfaceViewGame, MAX_MANA / 5);
    }

    @Override
    protected void addMana() {
        mana += MAX_MANA / (TICK * 15f);
    }

    @Override
    public boolean activateSkill(Game game) {
       if (super.activateSkill(game)){
           new AddSlimeEvent(position.copy(), game.getSynchronizedTick()).send();
           Log.d("Slime", "adding SlimeTrail");
           return true;
       }
        return false;
    }
}
