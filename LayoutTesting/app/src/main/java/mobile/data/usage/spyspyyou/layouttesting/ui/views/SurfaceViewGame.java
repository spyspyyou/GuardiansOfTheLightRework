package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import mobile.data.usage.spyspyyou.layouttesting.game.Tick;

public class SurfaceViewGame extends SurfaceView implements SurfaceHolder.Callback{

    private int halfWidth = 0, halfHeight = 0;

    private int tileSide = 0;

    private boolean
            hasFocus = false,
            created = false;

    private float fingerDisplacementX, fingerDisplacementY;

    private GestureDetector clickDetector;

    public SurfaceViewGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        clickDetector = new GestureDetector(new ClickDetector());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        invalidate();
        halfWidth = getWidth() / 2;
        halfHeight = getHeight() / 2;
        int sideW = (int) (1.0 * getWidth() / Tick.MAX_TILES_IN_WIDTH), sideH = getHeight() / Tick.MAX_TILES_IN_HEIGHT;
        if (sideW > sideH) Log.i("SVGame", "chose width");
        else  Log.i("SVGame", "chose height");
        tileSide = Math.max(sideW, sideH);
        created = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (clickDetector.onTouchEvent(event))return true;
        if (event.getAction() == MotionEvent.ACTION_UP){
            hasFocus = false;
        }else {
            hasFocus = true;
            fingerDisplacementX = event.getX() - halfWidth;
            fingerDisplacementY = event.getY() - halfHeight;
        }
        return true;
    }

    public void drawToScreen(Canvas canvas){
        if (canvas != null)getHolder().unlockCanvasAndPost(canvas);
    }

    public Canvas getCanvas(){
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
        } catch (Exception e){
            e.printStackTrace();
        }
        return canvas;
    }

    public boolean hasUserFocus(){
        return hasFocus;
    }

    public double getUserDirection(){
        double radiusDistance = Math.sqrt(Math.pow(fingerDisplacementX, 2) + Math.pow(fingerDisplacementY, 2));
        double userDirection = Math.acos(fingerDisplacementX / radiusDistance);
        if (fingerDisplacementY <= 0) userDirection *= -1;
        return userDirection;
    }

    public int getHalfWidth(){
        return halfWidth;
    }

    public int getHalfHeight(){
        return halfHeight;
    }

    public boolean isCreated() {
        return created;
    }

    public int getTileSide() {
        return tileSide;
    }

    private class ClickDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //todo:check if a player was clicked
            return false;
        }
    }

}
