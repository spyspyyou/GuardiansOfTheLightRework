package mobile.data.usage.spyspyyou.gametest.game.entities;

import android.graphics.Canvas;
import android.graphics.Color;

import mobile.data.usage.spyspyyou.gametest.R;
import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.Tick;
import mobile.data.usage.spyspyyou.gametest.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;


public class Gum extends Entity {

    public static final double
            //number is Tiles per second
            GUM_SPEED = 7.5 / Tick.TICK;

    private static final int
            COLOR_ENEMY = Color.RED,
            COLOR_ALLY = Color.GREEN;

    private boolean teamBlue;

    private final Vector2D VELOCITY;

    private boolean ally = false;

    public Gum(Vector2D entityPosition, double direction, boolean teamBlue) {
        super(entityPosition, (int)(SurfaceViewGame.getTileSide() * 0.5), (int) (SurfaceViewGame.getTileSide() * 0.5), R.drawable.gum);
        this.teamBlue = teamBlue;
        VELOCITY = new Vector2D(Math.cos(direction), Math.sin(direction));
        VELOCITY.scaleTo(GUM_SPEED);
    }

    public void jump(Game game, int tickDifference){
        position.add(tickDifference * VELOCITY.x, tickDifference * VELOCITY.y);
    }

    @Override
    public void update(Game game) {
        position.add(VELOCITY);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
    }

    public boolean isBlue(){
        return teamBlue;
    }
}
