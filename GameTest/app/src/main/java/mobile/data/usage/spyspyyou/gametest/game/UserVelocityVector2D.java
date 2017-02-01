package mobile.data.usage.spyspyyou.gametest.game;


import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.TICK;

public class UserVelocityVector2D extends Vector2D {

    private static final double
            //in tiles per second
            MAX_VELOCITY = 5.5 / TICK,
            SLOW = 0.2;

    public UserVelocityVector2D(double x, double y) {
        super(x, y);
    }

    public Vector2D getVelocity(boolean isSlowed){
        Vector2D v = new Vector2D(this).scale(MAX_VELOCITY);
        if (!isSlowed)return v;
        return v.scale(SLOW);
    }

}
