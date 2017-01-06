package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.game.Tick;
import mobile.data.usage.spyspyyou.layouttesting.utils.paints.BorderPaint;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public class LightBulb extends Entity implements Tick{

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
            if (!isInRange())cancelPickingUp();
            pickingProgress += PROGRESS_PER_TICK;
            if (pickingProgress >= PICKING_COMPLETE)requestBulb();
        }else if(isInRange())startPickingUp();
    }

    private boolean isInRange(){
        return position.squareDistance(Game.getUserPosition()) < PICKUP_DISTANCE_SQUARE;
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
