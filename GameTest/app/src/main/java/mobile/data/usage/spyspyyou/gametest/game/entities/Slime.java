package mobile.data.usage.spyspyyou.gametest.game.entities;

import android.util.Log;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.events.global.SlimeEvent;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.ID_SLIME;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.TICK;


public class Slime extends User {

    public Slime(boolean teamBlue) {
        super(teamBlue, ID_SLIME, MAX_MANA / 5);
    }

    @Override
    protected void addMana() {
        mana += MAX_MANA / (TICK * 15f);
    }

    @Override
    public boolean activateSkill(Game game) {
       if (super.activateSkill(game)){
           new SlimeEvent(position.copy(), game.getSynchronizedTick()).send();
           Log.i("Slime", "adding SlimeTrail");
           return true;
       }
        return false;
    }
}
