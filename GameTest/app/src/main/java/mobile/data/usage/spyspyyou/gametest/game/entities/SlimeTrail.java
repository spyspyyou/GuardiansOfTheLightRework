package mobile.data.usage.spyspyyou.gametest.game.entities;


import mobile.data.usage.spyspyyou.gametest.R;
import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

public class SlimeTrail extends Entity {

    private final int DEATH_TICK;

    public SlimeTrail(Vector2D entityPosition, int deathTick) {
        super(entityPosition, R.drawable.slime_trail);
        DEATH_TICK = deathTick;
    }

    @Override
    public void update(Game game) {
        //todo:check user hit.
    }

    public boolean isDead(int synchronizedTick){
        return  DEATH_TICK <= synchronizedTick;
    }
}
