package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.BitmapManager;
import mobile.data.usage.spyspyyou.layouttesting.game.Tick;
import mobile.data.usage.spyspyyou.layouttesting.game.VelocityVector2D;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.Player;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.User;
import mobile.data.usage.spyspyyou.layouttesting.utils.ColorPaint;
import mobile.data.usage.spyspyyou.layouttesting.utils.RingPaint;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_ALLY;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_ENEMY;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_USER;

public class SurfaceViewGame extends SurfaceView implements SurfaceHolder.Callback{

    public static final byte
            LEFT = 0,
            RIGHT = 1,
            RANDOM = 2;

    private int
            halfWidth = 0,
            halfHeight = 0,
            tileSide = 0,
            margin;

    private boolean
            hasFocus = false,
            created = false;

    private Vector2D
            fingerDisplacement = new Vector2D(0, 0);

    private double
            userDirection;

    private VelocityVector2D
            userVelocity = new VelocityVector2D(0, 0);

    private VirtualSurface
            surfaceJoystick,
            surfaceMiniMap,
            buttonSkill,
            buttonParticle;

    private GestureDetector clickDetector;

    public SurfaceViewGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        clickDetector = new GestureDetector(new ClickDetector());
        margin = (int) getResources().getDimension(R.dimen.activity_margin_normal);
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
        synchronized (getHolder()) {
            if (
                    (surfaceMiniMap.preClaimTouchEvent(event) && surfaceMiniMap.onTouchEvent(event))
                            || (buttonSkill.preClaimTouchEvent(event) && buttonSkill.onTouchEvent(event))
                            || (buttonParticle.preClaimTouchEvent(event) && buttonParticle.onTouchEvent(event))
                            || clickDetector.onTouchEvent(event)
                            || event.getAction() == MotionEvent.ACTION_UP
                    ) {
                hasFocus = false;
            } else if (surfaceJoystick.preClaimTouchEvent(event) || ((SurfaceJoystick)surfaceJoystick).active){
                surfaceJoystick.onTouchEvent(event);
            } else {
                hasFocus = true;
                fingerDisplacement.set(event.getX() - halfWidth, event.getY() - halfHeight);
            }
        }
        return true;
    }

    public void setup(int controlType, Bitmap map, Player[] players){
        switch(controlType){
            case LEFT:
                createLeft(map, players);
                break;
            case RIGHT:
                createRight(map, players);
                break;
            case RANDOM:
            default:
                createRandom(map, players);
        }
    }

    private void createLeft(Bitmap map, Player[] players){
        surfaceJoystick = new SurfaceJoystick(new Rect(0, 0, (int) getResources().getDimension(R.dimen.joystick_surface_width), getHeight()));
        int miniMapSize = (int) getResources().getDimension(R.dimen.mini_map_size);
        surfaceMiniMap = new SurfaceMiniMap(new Rect(getWidth() - margin - miniMapSize, margin, getWidth() - margin, margin + miniMapSize), map, players);
        buttonSkill = new FillingButton(new Rect(0, 0, 0, 0));
        buttonParticle = new FillingButton(new Rect(0, 0, 0, 0));
    }

    private void createRight(Bitmap map, Player[] players){
        surfaceJoystick = new SurfaceJoystick(new Rect(0, 0, 0, 0));
        surfaceMiniMap = new SurfaceMiniMap(new Rect(0, 0, 0, 0), map, players);
        buttonSkill = new FillingButton(new Rect(0, 0, 0, 0));
        buttonParticle = new FillingButton(new Rect(0, 0, 0, 0));
    }

    private void createRandom(Bitmap map, Player[] players){
        surfaceJoystick = new SurfaceJoystick(new Rect(0, 0, 0, 0));
        surfaceMiniMap = new SurfaceMiniMap(new Rect(0, 0, 0, 0), map, players);
        buttonSkill = new FillingButton(new Rect(0, 0, 0, 0));
        buttonParticle = new FillingButton(new Rect(0, 0, 0, 0));
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

    public void render(Canvas canvas){
        if (canvas != null){
            surfaceJoystick.render(canvas);
            surfaceMiniMap.render(canvas);
            buttonSkill.render(canvas);
            buttonParticle.render(canvas);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public double getUserDirection() {
        if (hasFocus) {
            synchronized (getHolder()) {
                userDirection = Math.acos(fingerDisplacement.x / fingerDisplacement.getLength());
                if (fingerDisplacement.y <= 0) userDirection *= -1;
                return userDirection;
            }
        }
        return userDirection;
    }

    public VelocityVector2D getUserVelocity(){
        return userVelocity;
    }

    public int getTileSide() {
        return tileSide;
    }

    public boolean isCreated() {
        return created;
    }

    public Vector2D getCenter(){
        return new Vector2D(halfWidth, halfHeight);
    }

    private class ClickDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //todo:check if a player was clicked
            return false;
        }
    }

    private abstract class VirtualSurface {

        protected final Rect POSITION;

        private VirtualSurface(Rect position){
            POSITION = position;
        }

        private boolean preClaimTouchEvent(MotionEvent motionEvent){
            return motionEvent.getX() > POSITION.left && motionEvent.getY() > POSITION.top&& motionEvent.getX() < POSITION.right && motionEvent.getY() < POSITION.bottom;
        }

        protected abstract boolean onTouchEvent(MotionEvent motionEvent);

        protected abstract void render(Canvas canvas);
    }

    private class SurfaceJoystick extends VirtualSurface implements Tick{

        private float joystickRadius;

        private Paint
                paintJoystickRing,
                paintJoystickCenter;

        private Vector2D
                joystickPosition = new Vector2D(0, 0),
                fingerDisplacement = new Vector2D(0, 0),
                centerDisplacement = new Vector2D(0, 0);

        private boolean
                active = false,
                dataChanged = false;

        private SurfaceJoystick(Rect position) {
            super(position);
            joystickRadius = getResources().getDimension(R.dimen.joystick_size);

            paintJoystickCenter = new Paint();
            paintJoystickCenter.setColor(COLOR_VALUE_BAR_BACKGROUND);
            paintJoystickRing = new RingPaint((int)(joystickRadius / 2), COLOR_VALUE_BAR_BACKGROUND);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            dataChanged = true;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                active = false;
                fingerDisplacement.set(0, 0);
            }else if (event.getAction() == MotionEvent.ACTION_DOWN && !active) {
                joystickPosition.set(event.getX(), event.getY());
                active = true;
            }else if (active){
                fingerDisplacement.set(event.getX() - joystickPosition.x, event.getY() - joystickPosition.y);
            }else{
                return false;
            }
            return true;
        }

        public void render(Canvas canvas) {
            updateData();
            if (canvas != null && active) {
                canvas.drawCircle(joystickPosition.getFloatX(), joystickPosition.getFloatY(), joystickRadius, paintJoystickRing);
                canvas.drawCircle(joystickPosition.getFloatX() + centerDisplacement.getFloatX(), joystickPosition.getFloatY() + centerDisplacement.getFloatY(), joystickRadius / 2, paintJoystickCenter);
            }
        }

        private void updateData() {
            if (!dataChanged) return;
            dataChanged = false;
            centerDisplacement.set(fingerDisplacement);

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

    }

    private class SurfaceMiniMap extends VirtualSurface{
        private int
                size;

        private float
                miniMapTileSizeSmall,
                miniMapTileSizeBig;

        private Rect
                bigRect;

        private Paint
                painMiniMapSmall = new Paint(),
                colorUser = new ColorPaint(COLOR_VALUE_USER),
                colorAlly = new ColorPaint(COLOR_VALUE_ALLY),
                colorEnemy = new ColorPaint(COLOR_VALUE_ENEMY);

        private Player[] players;

        private boolean
                big = false;

        private Bitmap bitmap;

        private SurfaceMiniMap(Rect position, Bitmap map, Player[] mPlayers) {
            super(position);
            bitmap = map;
            players = mPlayers;

            size = (int) getResources().getDimension(R.dimen.mini_map_size);

            bigRect = new Rect(halfWidth - getHeight() / 2, margin, halfWidth + getHeight() / 2, getHeight() - 2 * margin);

            miniMapTileSizeSmall = (1f * size) / bitmap.getWidth();
            miniMapTileSizeBig = (1f * getHeight()) /bitmap.getWidth();

            painMiniMapSmall.setAlpha(0xaa);

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                big = !big;
                return true;
            }
            return false;
        }

        public void render(Canvas canvas) {
            if (canvas != null) {
                if (big) {
                    canvas.drawBitmap(bitmap, null, bigRect, null);
                    canvas.drawBitmap(BitmapManager.getBitmap(R.drawable.cross), null, POSITION, painMiniMapSmall);
                    drawPlayers(canvas, bigRect, miniMapTileSizeBig);
                } else {
                    canvas.drawBitmap(bitmap, null, POSITION, painMiniMapSmall);
                    drawPlayers(canvas, POSITION, miniMapTileSizeSmall);
                }
            }
        }

        private void drawPlayers(Canvas canvas, Rect rect, float tileSide){
            Paint currentColor;
            for (Player player:players){
                if (player instanceof User){
                    currentColor = colorUser;
                } else if (player.isAlly()){
                    currentColor = colorAlly;
                }else if (true){
                    currentColor = colorEnemy;
                }else{
                    continue;
                }
                canvas.drawCircle(rect.left + player.getPosition().getFloatX() * tileSide, rect.top + player.getPosition().getFloatY() * tileSide, player.getRadius() / tileSide * tileSide, currentColor);
            }
        }

    }

    private class FillingButton extends VirtualSurface{

        private FillingButton(Rect position) {
            super(position);
        }

        @Override
        protected boolean onTouchEvent(MotionEvent motionEvent) {
            return true;
        }

        @Override
        protected void render(Canvas canvas) {

        }
    }

}
