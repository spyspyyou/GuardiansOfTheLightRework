package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_GHOST;

public class Ghost extends User {

    public Ghost(Vector2D entityPosition, int size, SurfaceViewGame surfaceViewGame) {
        super(entityPosition, size, ID_GHOST, surfaceViewGame, 100);
    }

    @Override
    protected void addMana() {

    }

    @Override
    public boolean activateSkill(Game game) {
        return super.activateSkill(game);
    }
}
