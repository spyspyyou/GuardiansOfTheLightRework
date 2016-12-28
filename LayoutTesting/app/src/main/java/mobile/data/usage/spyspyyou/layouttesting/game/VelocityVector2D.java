package mobile.data.usage.spyspyyou.layouttesting.game;

import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public class VelocityVector2D extends Vector2D {

    private static final double
            MAX_VELOCITY = 1.2,
            SLOW = 0.2;

    public VelocityVector2D(double x, double y) {
        super(x, y);
    }

    public Vector2D getVelocity(boolean isSlowed){
        Vector2D v = new Vector2D(this).scale(MAX_VELOCITY);
        if (isSlowed)return v;
        return v.scale(SLOW);
    }

}
