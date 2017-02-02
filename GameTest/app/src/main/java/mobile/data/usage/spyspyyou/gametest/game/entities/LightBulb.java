package mobile.data.usage.spyspyyou.gametest.game.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import mobile.data.usage.spyspyyou.gametest.R;
import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.Tick;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;
import mobile.data.usage.spyspyyou.gametest.utils.paints.BorderPaint;

public class LightBulb extends Entity implements Tick {

    private static final float
            PICKING_COMPLETE = 360,
            PROGRESS_PER_TICK = PICKING_COMPLETE / (8f * TICK),
            PICKUP_DISTANCE_SQUARE = 2.1f;

    private boolean
            isPicking = false;

    private float
            pickingProgress = 0;

    private Paint
            ringPaint;

    public LightBulb(Vector2D entityPosition) {
        super(entityPosition, R.drawable.light_bulb_on_square);
        ringPaint = new BorderPaint(15, Color.YELLOW);
    }

    @Override
    public void update(Game game) {
        if (isPicking){
            if (!isInRange(game))cancelPickingUp();
            pickingProgress += PROGRESS_PER_TICK;
            if (pickingProgress >= PICKING_COMPLETE)requestBulb();
        }else if(isInRange(game))startPickingUp();
    }

    private boolean isInRange(Game game){
        return position.squareDistance(game.getUserPosition()) < PICKUP_DISTANCE_SQUARE;
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (isPicking)canvas.drawArc(rect, -90, pickingProgress, false, ringPaint);
    }

    private void startPickingUp(){
        isPicking = true;
        pickingProgress = 0;
    }

    private void cancelPickingUp(){
        isPicking = false;
    }

    private void requestBulb(){
        //todo:requestLightBulb
    }
}
