package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.BitmapManager;
import mobile.data.usage.spyspyyou.layouttesting.game.VelocityVector2D;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public class SurfaceViewJoystick extends SurfaceView implements SurfaceHolder.Callback{

    private float joystickRadius;

    private Bitmap
            ring,
            center;

    private VelocityVector2D
            userVelocity = new VelocityVector2D(0, 0);
    private Vector2D
            joystickPosition = new Vector2D(0, 0),
            fingerDisplacement = new Vector2D(0, 0),
            centerDisplacement = new Vector2D(0, 0);

    private double userDirection;

    private boolean
            active = false,
            dataChanged = false,
            created = false;

    public SurfaceViewJoystick(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setZOrderMediaOverlay(true);
        joystickRadius = getResources().getDimension(R.dimen.joystick_size);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        synchronized (getHolder()){
            dataChanged = true;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                active = false;
                fingerDisplacement.set(0, 0);
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN && !active) {
                joystickPosition.set(event.getX(), event.getY());
                active = true;
            }

            fingerDisplacement.set(event.getX() - joystickPosition.x, event.getY() - joystickPosition.y);
            return true;
        }
    }

    public void updateData() {
        synchronized (getHolder()) {
            if (!dataChanged) return;
            dataChanged = false;

            centerDisplacement.set(fingerDisplacement);
        }

        if (!active || centerDisplacement.has0Length()) {
            userVelocity.set(0, 0);
            return;
        }

        if (centerDisplacement.getLength() > joystickRadius) {
            centerDisplacement.scaleTo(joystickRadius);
        }

        userVelocity.set(centerDisplacement.x / joystickRadius, centerDisplacement.y / joystickRadius);

        userDirection = Math.acos(centerDisplacement.x / centerDisplacement.getLength());
        if (centerDisplacement.y <= 0) userDirection *= -1;
    }

    public void render() {
        if (ring == null) {
            ring = Bitmap.createScaledBitmap(BitmapManager.getBitmap(R.drawable.joystick_button_ring), (int) (joystickRadius * 2), (int) (joystickRadius * 2), false);
            center = Bitmap.createScaledBitmap(BitmapManager.getBitmap(R.drawable.joystick_button_middle), (int) joystickRadius, (int) joystickRadius, false);
        }

        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            synchronized (getHolder()) {
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                    if (active) {
                        canvas.drawBitmap(ring, joystickPosition.getFloatX() - ring.getWidth() / 2f, joystickPosition.getFloatY() - ring.getHeight() / 2f, null);
                        canvas.drawBitmap(center, joystickPosition.getFloatX() - center.getWidth() / 2f + centerDisplacement.getFloatX(), joystickPosition.getFloatY() - center.getHeight() / 2f + centerDisplacement.getFloatY(), null);

                    }
                }
            }
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    public VelocityVector2D getUserVelocity() {
        return userVelocity;
    }

    public double getUserDirection() {
        return userDirection;
    }

    public boolean isCreated() {
        return created;
    }

    public boolean isActive(){
        return active;
    }
}
