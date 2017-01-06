package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.game.Tick;
import mobile.data.usage.spyspyyou.layouttesting.utils.paints.ColorPaint;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

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
        super(entityPosition, 0);
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
        Game.updateScreenPosition(position, screenPosition);
        canvas.drawCircle(screenPosition.getFloatX() , screenPosition.getFloatY(), 15, ((ally)?GUM_ALLY:GUM_ENEMY));
    }
}
