package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
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
            created = false;

    private Vector2D
            fingerDisplacement = new Vector2D(0, 0);

    private double
            userDirection = 0;

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
        if (isActionDown(event)) {
            Vector2D position = getActionPosition(event);
            //assign the Event to a surface
            if (surfaceMiniMap.preClaimTouchEvent(position)) {
            } else if (buttonSkill.preClaimTouchEvent(position)) {
            } else if (buttonParticle.preClaimTouchEvent(position)) {
            } else if (clickDetector.onTouchEvent(event)) {
            } else if (surfaceJoystick.preClaimTouchEvent(position)) {
            } else {
            }
            Log.i("SVGame", "touchDown");
        }

        //distribute the event
        surfaceMiniMap.onTouchEvent(event);
        buttonSkill.onTouchEvent(event);
        buttonParticle.onTouchEvent(event);
        onTouch(event);
        surfaceJoystick.onTouchEvent(event);

        if (isActionUp(event)) {
            //release the Event assignment
            Log.i("SVGame", "release");
        }
        return true;
    }

    private void onTouch(MotionEvent event){
        synchronized (getHolder()) {
            if (isActionUp(event))
                fingerDisplacement.set(0, 0);
            else
                fingerDisplacement.set(event.getX() - halfWidth, event.getY() - halfHeight);
        }
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
        if (!((SurfaceJoystick)surfaceJoystick).active && ! fingerDisplacement.has0Length()) {
            synchronized (getHolder()) {
                userDirection = Math.acos(fingerDisplacement.x / fingerDisplacement.getLength());
                if (fingerDisplacement.y <= 0) userDirection *= -1;
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

    private boolean isActionDown(MotionEvent motionEvent){
        return motionEvent.getAction() == MotionEvent.ACTION_DOWN || MotionEventCompat.getActionMasked(motionEvent) == MotionEventCompat.ACTION_POINTER_DOWN;
    }

    private boolean isActionUp(MotionEvent motionEvent){
        return motionEvent.getAction() == MotionEvent.ACTION_UP || MotionEventCompat.getActionMasked(motionEvent) == MotionEventCompat.ACTION_POINTER_UP;
    }

    private Vector2D getActionPosition(MotionEvent motionEvent){
        int index = MotionEventCompat.getActionIndex(motionEvent);
        return new Vector2D(motionEvent.getX(index), motionEvent.getY(index));
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

        protected boolean preClaimTouchEvent(Vector2D position){
            return POSITION.contains(position.getIntX(), position.getIntY());
        }

        protected abstract void onTouchEvent(MotionEvent motionEvent);

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
        protected boolean preClaimTouchEvent(Vector2D position) {
            return super.preClaimTouchEvent(position) && !active;
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            dataChanged = true;
            if (!active) {
                joystickPosition.set(event.getX(), event.getY());
                active = true;
            } else if (isActionUp(event)) {
                active = false;
                fingerDisplacement.set(0, 0);
            } else{
                fingerDisplacement.set(event.getX() - joystickPosition.x, event.getY() - joystickPosition.y);
            }
        }

        public void render(Canvas canvas) {
            updateData();
            if (canvas != null && active) {
                canvas.drawCircle(joystickPosition.getFloatX(), joystickPosition.getFloatY(), joystickRadius * 0.75f, paintJoystickRing);
                canvas.drawCircle(joystickPosition.getFloatX() + centerDisplacement.getFloatX(), joystickPosition.getFloatY() + centerDisplacement.getFloatY(), joystickRadius / 2, paintJoystickCenter);
            }
        }

        private void updateData() {
            if (!dataChanged) return;
            dataChanged = false;
            synchronized (getHolder()) {
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

    }

    private abstract class VirtualButton extends VirtualSurface{

        private VirtualButton(Rect position) {
            super(position);
        }

        @Override
        protected void onTouchEvent(MotionEvent motionEvent) {
            if ((motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_POINTER_DOWN)){
                onClick();
            }
        }

        protected abstract void onClick();
    }

    private class SurfaceMiniMap extends VirtualButton{

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

        private Player[]
                players;

        private boolean
                big = false;

        private Bitmap
                bitmap;

        private SurfaceMiniMap(Rect position, Bitmap map, Player[] mPlayers) {
            super(position);
            bitmap = map;
            players = mPlayers;

            size = (int) getResources().getDimension(R.dimen.mini_map_size);

            bigRect = new Rect(halfWidth - getHeight() / 2 + margin, margin, halfWidth + getHeight() / 2 - margin, getHeight() - margin);

            miniMapTileSizeSmall = (1f * size) / bitmap.getWidth();
            miniMapTileSizeBig = (1f * (getHeight() - 2 * margin)) / bitmap.getWidth();
            painMiniMapSmall.setAlpha(0xaa);
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

        private void drawPlayers(Canvas canvas, Rect rect, float miniMapTileSide){
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
                float
                        cx = rect.left + player.getPosition().getFloatX() * miniMapTileSide,
                        cy = rect.top + player.getPosition().getFloatY() * miniMapTileSide,
                        r = player.getRadius() / tileSide * miniMapTileSide;
                canvas.drawCircle(cx, cy, r, currentColor);
            }
        }

        @Override
        protected void onClick() {
            big = !big;
        }
    }

    private class FillingButton extends VirtualButton{

        private FillingButton(Rect position) {
            super(position);
        }

        @Override
        protected void onClick() {

        }

        @Override
        protected void render(Canvas canvas) {

        }
    }

}
