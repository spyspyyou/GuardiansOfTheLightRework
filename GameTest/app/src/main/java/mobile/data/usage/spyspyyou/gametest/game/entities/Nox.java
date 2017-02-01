package mobile.data.usage.spyspyyou.gametest.game.entities;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.ID_GHOST;

public class Nox extends User {

    public Nox(Vector2D entityPosition) {
        super(entityPosition, ID_GHOST, 100);
    }

    @Override
    protected void addMana() {

    }

    @Override
    public boolean activateSkill(Game game) {
        return super.activateSkill(game);
    }
}
