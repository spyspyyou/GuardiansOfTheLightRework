package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.BitmapManager;
import mobile.data.usage.spyspyyou.layouttesting.game.Vector2D;

public class SurfaceViewJoystick extends SurfaceView implements SurfaceHolder.Callback{

    private int joystickRadius;

    private Vector2D userVelocity = new Vector2D(0, 0);

    private double userDirection;

    private float joystickCoordinateX, joystickCoordinateY, fingerDisplacementX, fingerDisplacementY;

    private boolean
            active = false,
            dataChanged = false,
            created = false;

    public SurfaceViewJoystick(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        invalidate();
        joystickRadius = getWidth() / 2;
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
        synchronized (getHolder()){
            dataChanged = true;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                active = false;
                fingerDisplacementX = fingerDisplacementY = 0;
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN && !active) {
                joystickCoordinateX = event.getX();
                joystickCoordinateY = event.getY();
                active = true;
            }
            fingerDisplacementX = event.getX() - joystickCoordinateX;
            fingerDisplacementY = event.getY() - joystickCoordinateY;
            return true;
        }
    }

    public void render(){
        synchronized (getHolder()){
            updateData();
            if (!active)return;
            Canvas canvas = null;
            try {
                canvas = getHolder().lockCanvas(null);
                synchronized (getHolder()) {
                    if (canvas != null) {
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                        canvas.drawBitmap(BitmapManager.getBitmap(R.drawable.joystick_button_ring), joystickCoordinateX, joystickCoordinateY, null);
                        canvas.drawBitmap(BitmapManager.getBitmap(R.drawable.joystick_button_middle), joystickCoordinateX, joystickCoordinateY, null);
                    }
                }
            } finally {
                if (canvas != null) {
                    // if we locked the Canvas before we have to unlock it now and give the System the Canvas to draw back
                    getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public Vector2D getUserVelocity() {
        updateData();
        return userVelocity;
    }

    public double getUserDirection() {
        updateData();
        return userDirection;
    }

    private void updateData(){
        synchronized (getHolder()){
            if (!dataChanged)return;
            dataChanged = false;

            if (!active || (fingerDisplacementX == 0 && fingerDisplacementY == 0)){
                userVelocity = new Vector2D(0, 0);
                return;
            }

            double radiusDistance = Math.sqrt(Math.pow(fingerDisplacementX, 2) + Math.pow(fingerDisplacementY, 2));
            if (radiusDistance > joystickRadius){
                fingerDisplacementX /= radiusDistance;
                fingerDisplacementX *= joystickRadius;

                fingerDisplacementY /= radiusDistance;
                fingerDisplacementY *= joystickRadius;
            }

            userVelocity = new Vector2D(1.0 * fingerDisplacementX / joystickRadius, 1.0 * fingerDisplacementY / joystickRadius);

            userDirection = Math.acos(fingerDisplacementX / radiusDistance);
            if (fingerDisplacementY <= 0) userDirection *= -1;
        }
    }

    public boolean isCreated() {
        return created;
    }
}
