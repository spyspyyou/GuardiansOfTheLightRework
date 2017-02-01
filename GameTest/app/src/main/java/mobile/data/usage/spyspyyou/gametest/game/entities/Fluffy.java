package mobile.data.usage.spyspyyou.gametest.game.entities;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.ID_FLUFFY;

public class Fluffy extends User {

    public Fluffy(Vector2D entityPosition) {
        super(entityPosition, ID_FLUFFY, MAX_MANA);
    }

    @Override
    public void update(Game game) {
        super.update(game);
    }

    @Override
    protected void addMana() {
        mana += velocity.getLength();
    }

    @Override
    public boolean activateSkill(Game game) {
       if (super.activateSkill(game)){
           return true;
       }
        return false;
    }
}
