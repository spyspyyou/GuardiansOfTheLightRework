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

public class SurfaceViewJoystick extends SurfaceView implements SurfaceHolder.Callback{

    private int joystickRadius;

    private float userVelocityX = 0, userVelocityY = 0;

    private double userDirection;

    private float joystickCoordinateX, joystickCoordinateY, fingerDisplacementX, fingerDisplacementY;

    private boolean active = false, dataChanged = false;

    public SurfaceViewJoystick(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        invalidate();
        joystickRadius = getWidth() / 2;
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
            if (event.getAction() == MotionEvent.ACTION_UP) {
                active = false;
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN && !active) {
                joystickCoordinateX = event.getX();
                joystickCoordinateY = event.getY();
                active = true;
            }

            fingerDisplacementX = event.getX() - joystickCoordinateX;
            fingerDisplacementY = event.getY() - joystickCoordinateY;

            dataChanged = true;
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

                        canvas.drawBitmap(BitmapManager.getBitmap(R.drawable.joystick_button_ring), joystickCoordinateX - joystickRadius, joystickCoordinateY - joystickRadius, null);
                        canvas.drawBitmap(BitmapManager.getBitmap(R.drawable.joystick_button_middle), joystickCoordinateX - joystickRadius + fingerDisplacementX, joystickCoordinateY - joystickRadius + fingerDisplacementY, null);
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

    public float getUserVelocityX() {
        if (!active) return 0;
        updateData();
        return userVelocityX;
    }

    public float getUserVelocityY() {
        if (!active) return 0;
        updateData();
        return userVelocityY;
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
                userVelocityX = userVelocityY = 0;
                return;
            }

            double radiusDistance = Math.sqrt(Math.pow(fingerDisplacementX, 2) + Math.pow(fingerDisplacementY, 2));
            if (radiusDistance > joystickRadius){
                fingerDisplacementX /= radiusDistance;
                fingerDisplacementX *= joystickRadius;

                fingerDisplacementY /= radiusDistance;
                fingerDisplacementY *= joystickRadius;
            }

            userVelocityX = fingerDisplacementX / joystickRadius;
            userVelocityY = fingerDisplacementY / joystickRadius;

            userDirection = Math.acos(fingerDisplacementX / radiusDistance);
            if (fingerDisplacementY <= 0) userDirection *= -1;
        }
    }
}
