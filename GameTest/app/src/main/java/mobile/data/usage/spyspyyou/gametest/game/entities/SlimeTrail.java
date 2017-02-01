package mobile.data.usage.spyspyyou.gametest.game.entities;


import mobile.data.usage.spyspyyou.gametest.R;
import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.TICK;

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
            //todo:local sweetTimeout Event
        }
        if (game.getUserPosition().squareDistance(position) < SurfaceViewGame.getTileSide()/2){
            //user touches the slime
            //todo:send slime hit event
        }
    }
}
