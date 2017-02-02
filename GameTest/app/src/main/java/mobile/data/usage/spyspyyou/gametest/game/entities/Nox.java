package mobile.data.usage.spyspyyou.gametest.game.entities;

import mobile.data.usage.spyspyyou.gametest.game.Game;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.ID_GHOST;

public class Nox extends User {

    public Nox(boolean teamBlue) {
        super(teamBlue, ID_GHOST, 100);
    }

    @Override
    protected void addMana() {

    }

    @Override
    public boolean activateSkill(Game game) {
        return super.activateSkill(game);
    }
}
