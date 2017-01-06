package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_FLUFFY;

public class Fluffy extends User {

    public Fluffy(Vector2D entityPosition, int size, SurfaceViewGame surfaceViewGame) {
        super(entityPosition, size, ID_FLUFFY, surfaceViewGame, MAX_MANA);
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
