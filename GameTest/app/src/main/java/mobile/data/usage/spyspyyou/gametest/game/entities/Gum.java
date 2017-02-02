package mobile.data.usage.spyspyyou.gametest.game.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import mobile.data.usage.spyspyyou.gametest.R;
import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.Tick;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;
import mobile.data.usage.spyspyyou.gametest.utils.paints.ColorPaint;


public class Gum extends Entity {

    private static final float
            //number is Tiles per second
            GUM_SPEED = 5.6f / Tick.TICK;

    private static final int
            COLOR_ENEMY = Color.RED,
            COLOR_ALLY = Color.GREEN;

    private static final Paint
            GUM_ENEMY = new ColorPaint(COLOR_ENEMY),
            GUM_ALLY = new ColorPaint(COLOR_ALLY);

    private final Vector2D VELOCITY;

    private boolean ally = false;

    public Gum(Vector2D entityPosition, Vector2D velocity) {
        super(entityPosition, R.drawable.spawn_tile);
        VELOCITY = velocity;
        VELOCITY.scaleTo(GUM_SPEED);
    }

    @Override
    public void update(Game game) {
        position.add(VELOCITY);
        //check hit box
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
    }
}
