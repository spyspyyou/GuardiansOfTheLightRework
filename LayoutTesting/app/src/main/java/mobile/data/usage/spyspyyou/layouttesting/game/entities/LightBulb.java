package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.Tick;
import mobile.data.usage.spyspyyou.layouttesting.utils.BorderPaint;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public class LightBulb extends Entity implements Tick{

    private static final float
            PICKING_COMPLETE = 360,
            PROGRESS_PER_TICK = PICKING_COMPLETE / (8f * TICK);

    private boolean
            isPicking = false;

    private float
            pickingProgress = 0;

    private Paint
            ringPaint;

    public LightBulb(Vector2D entityPosition, int size) {
        super(entityPosition, size, size, R.drawable.light_bulb_on_square);
        ringPaint = new BorderPaint(15, Color.YELLOW);
    }

    @Override
    public void update() {
        if (isPicking){
            pickingProgress += PROGRESS_PER_TICK;
            if (pickingProgress >= PICKING_COMPLETE)finishPickUp();
        }
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        canvas.drawArc(rect, -90, pickingProgress, false, ringPaint);
    }

    public void startPickingUp(){
        isPicking = true;
        pickingProgress = 0;
    }

    private void cancelPickingUp(){
        isPicking = false;

    }

    private void finishPickUp(){
        cancelPickingUp();
    }
}
