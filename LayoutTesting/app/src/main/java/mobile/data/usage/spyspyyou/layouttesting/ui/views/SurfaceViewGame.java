package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class SurfaceViewGame extends SurfaceView implements SurfaceHolder.Callback{

    Canvas matchingCanvas;
    SurfaceHolder surfaceHolder;

    private int width = 0, height = 0;

    public SurfaceViewGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        invalidate();
        width = getWidth();
        height = getHeight();
        matchingCanvas = new Canvas(Bitmap.createBitmap(width, height, ARGB_8888));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void render(Canvas canvas){
        Canvas c = null;
        try {
            // tell the System that it is not allowed to draw the Canvas while we are changing it
            // we get the Canvas of the GameSurface in return
            c = surfaceHolder.lockCanvas(null);
            // Google recommends to synchronize the drawing process with the SurfaceHolder
            synchronized (surfaceHolder) {
                // if we got a Canvas we can draw
                if (c != null) {
                    // erase the all drawing
                    c.drawColor(Color.BLACK);

                }
            }
        } finally {
            if (c != null) {
                // if we locked the Canvas before we have to unlock it now and give the System the Canvas to draw back
                surfaceHolder.unlockCanvasAndPost(c);
            }
        }
    }

    public Canvas getMatchingCanvas(){
        return matchingCanvas;
    }
}
