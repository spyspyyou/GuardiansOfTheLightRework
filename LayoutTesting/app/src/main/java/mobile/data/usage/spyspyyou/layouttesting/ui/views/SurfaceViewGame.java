package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class SurfaceViewGame extends SurfaceView implements SurfaceHolder.Callback{

    private int halfWidth = 0, halfHeight = 0;

    private boolean hasFocus = false;

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

        if (event.getAction() ==  MotionEvent.ACTION_DOWN ||event.getAction() == MotionEvent.ACTION_MOVE){
            hasFocus = true;
            fingerDisplacementX = event.getX() - halfWidth;
            fingerDisplacementY = event.getY() - halfHeight;
        }else if (event.getAction() == MotionEvent.ACTION_UP){
            hasFocus = false;
        }
        return false;
    }

    public void drawToScreen(Canvas canvas){
        getHolder().unlockCanvasAndPost(canvas);
    }

    public Canvas getCanvas(){
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas(null);
        } catch (Exception e){
            e.printStackTrace();
            canvas = new Canvas(Bitmap.createBitmap(getWidth(), getHeight(), ARGB_8888));
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

    private class ClickDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //todo:check if a player was clicked
            return false;
        }
    }

}
