package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceViewMiniMap extends SurfaceView implements SurfaceHolder.Callback{

    private boolean created = false;

    public SurfaceViewMiniMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
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

    public void render(){

    }

    public boolean isCreated() {
        return created;
    }
}
