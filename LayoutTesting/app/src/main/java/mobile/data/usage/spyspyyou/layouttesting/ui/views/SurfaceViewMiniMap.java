package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import mobile.data.usage.spyspyyou.layouttesting.R;

public class SurfaceViewMiniMap extends SurfaceView implements SurfaceHolder.Callback{

    private int size;

    private boolean
            created = false,
            big = false;

    private Bitmap bitmap;

    public SurfaceViewMiniMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setZOrderMediaOverlay(true);
        size = (int) getResources().getDimension(R.dimen.mini_map_size);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        created = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void setMap(Bitmap map){
        bitmap = map;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            big = !big;
            return true;
        }
        return false;
    }

    public void render(){
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            synchronized (getHolder()) {
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    if (big){
                        canvas.drawBitmap(bitmap, null, new Rect(0, 0, getWidth(), getHeight()), null);
                    }else{
                        canvas.drawBitmap(bitmap, null, new Rect(0,0, size, size), null);
                    }
                }
            }
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    public boolean isCreated() {
        return created;
    }
}
