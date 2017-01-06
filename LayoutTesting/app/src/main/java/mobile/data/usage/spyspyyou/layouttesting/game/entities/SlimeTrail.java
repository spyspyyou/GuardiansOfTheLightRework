package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.TICK;

public class SlimeTrail extends Entity {

    private static final int LIVE_SPAN_TICKS = 5 * TICK;

    private final int BIRTH_TICK;

    public SlimeTrail(Vector2D entityPosition, int birthTick) {
        super(entityPosition, R.drawable.slime_trail);
        BIRTH_TICK = birthTick + LIVE_SPAN_TICKS;
    }

    @Override
    public void update(Game game) {
        if (BIRTH_TICK <= game.getSynchronizedTick()){
            game.removeSlime(this);
        }
        if (Game.getUserPosition().squareDistance(position) < SurfaceViewGame.getTileSide()/2){
            //user touches the slime
            //send slime hit event
        }
    }
}
