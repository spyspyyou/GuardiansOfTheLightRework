package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.BitmapManager;
import mobile.data.usage.spyspyyou.layouttesting.game.Tick;
import mobile.data.usage.spyspyyou.layouttesting.game.VelocityVector2D;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.Player;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.User;
import mobile.data.usage.spyspyyou.layouttesting.utils.BorderPaint;
import mobile.data.usage.spyspyyou.layouttesting.utils.ColorPaint;
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
            created = false,
            joyStickActive = false;

    private double
            userDirection = 0;

    private VelocityVector2D
            userVelocity = new VelocityVector2D(0, 0);

    private SurfaceJoystick surfaceJoystick;

    private ArrayList<VirtualButton>
            buttons = new ArrayList<>();

    public SurfaceViewGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
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
        for (VirtualButton button:buttons)button.onTouchEvent(event);

        if (isActionDown(event))surfaceJoystick.claimTouchSeries(event);
        surfaceJoystick.onTouchEvent(event);
        if (isActionUp(event))surfaceJoystick.releaseTouchSeries(event);

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
        int miniMapSize = (int) getResources().getDimension(R.dimen.mini_map_size);
        surfaceJoystick = new SurfaceJoystick(new Rect(0, 0, (int) getResources().getDimension(R.dimen.joystick_surface_width), getHeight()));
        buttons.add(new SurfaceMiniMap(new Rect(getWidth() - margin - miniMapSize, margin, getWidth() - margin, margin + miniMapSize), map, players));
        buttons.add(new FillingButton(new Rect(0, 0, 0, 0)));
        buttons.add(new FillingButton(new Rect(0, 0, 0, 0)));
    }

    private void createRight(Bitmap map, Player[] players){
        int miniMapSize = (int) getResources().getDimension(R.dimen.mini_map_size);
        surfaceJoystick = new SurfaceJoystick(new Rect(0, 0, (int) getResources().getDimension(R.dimen.joystick_surface_width), getHeight()));
        buttons.add(new SurfaceMiniMap(new Rect(getWidth() - margin - miniMapSize, margin, getWidth() - margin, margin + miniMapSize), map, players));
        buttons.add(new FillingButton(new Rect(0, 0, 0, 0)));
        buttons.add(new FillingButton(new Rect(0, 0, 0, 0)));
    }

    private void createRandom(Bitmap map, Player[] players){
        int miniMapSize = (int) getResources().getDimension(R.dimen.mini_map_size);
        surfaceJoystick = new SurfaceJoystick(new Rect(0, 0, (int) getResources().getDimension(R.dimen.joystick_surface_width), getHeight()));
        buttons.add(new SurfaceMiniMap(new Rect(getWidth() - margin - miniMapSize, margin, getWidth() - margin, margin + miniMapSize), map, players));
        buttons.add(new FillingButton(new Rect(0, 0, 0, 0)));
        buttons.add(new FillingButton(new Rect(0, 0, 0, 0)));
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
            for (VirtualButton button: buttons){
                button.render(canvas);
            }

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public double getUserDirection() {
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

    //----------------------------------------------------------------------------------------------------------------------------
    // MotionEvent getter methods

    private boolean isActionDown(MotionEvent event){
        int action = event.getActionMasked();
        return action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN;
    }

    private boolean isActionUp(MotionEvent event){
        int action = event.getActionMasked();
        return action == MotionEvent.ACTION_UP  || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL;
    }

    private Vector2D getActionPosition(MotionEvent event){
        int index = MotionEventCompat.getActionIndex(event);
        return new Vector2D(event.getX(index), event.getY(index));
    }

    //----------------------------------------------------------------------------------------------------------------------------
    // Virtual Surfaces

    private abstract class VirtualSurface {

        protected final Rect POSITION;

        private VirtualSurface(Rect position){
            POSITION = position;
        }

        protected boolean contains(Vector2D position){
            return POSITION.contains(position.getIntX(), position.getIntY());
        }

        protected abstract boolean onTouchEvent(MotionEvent event);

        protected abstract void render(Canvas canvas);
    }

    private class SurfaceJoystick extends VirtualSurface implements Tick{

        private int
                pointerID;

        private float
                joystickRadius;

        private Paint
                paintJoystickRing,
                paintJoystickCenter;

        private Vector2D
                joystickPosition = new Vector2D(0, 0),
                fingerDisplacement = new Vector2D(0, 0),
                centerDisplacement = new Vector2D(0, 0);

        private boolean
                dataChanged = false;

        private SurfaceJoystick(Rect position) {
            super(position);
            joystickRadius = getResources().getDimension(R.dimen.joystick_size);

            paintJoystickCenter = new Paint();
            paintJoystickCenter.setColor(COLOR_VALUE_BAR_BACKGROUND);
            paintJoystickRing = new BorderPaint((int)(joystickRadius / 2), COLOR_VALUE_BAR_BACKGROUND);
        }

        private boolean claimTouchSeries(MotionEvent event) {
            Vector2D position = getActionPosition(event);
            if (POSITION.contains(position.getIntX(), position.getIntY()) && !joyStickActive) {
                Log.d("SVGame", "joystick start");
                joystickPosition.set(position.x, position.y);
                fingerDisplacement.set(0, 0);
                joyStickActive = true;
                pointerID = event.getPointerId(event.getActionIndex());
                return true;
            }
            return false;
        }

        private void releaseTouchSeries(MotionEvent event){
            if (event.getPointerId(event.getActionIndex()) != pointerID)return;
            Log.d("SVGame", "joystick stop");
            joyStickActive = false;
            fingerDisplacement.set(0, 0);
        }

        @Override
        protected boolean onTouchEvent(MotionEvent event) {
            if (!joyStickActive)return false;
            dataChanged = true;

            fingerDisplacement.set(event.getX(event.findPointerIndex(pointerID)) - joystickPosition.x, event.getY(event.findPointerIndex(pointerID)) - joystickPosition.y);
            return true;
        }

        public void render(Canvas canvas) {
            updateData();
            if (canvas != null && joyStickActive) {
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

            if (!joyStickActive || centerDisplacement.has0Length()) {
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
        protected boolean onTouchEvent(MotionEvent event) {
            if (isActionDown(event) && contains(getActionPosition(event))){
                onClick();
                return true;
            }
            return false;
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
                paintMiniMapSmall = new Paint(),
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

            paintMiniMapSmall.setAlpha(0xaa);
        }

        public void render(Canvas canvas) {
            if (canvas != null) {
                if (big) {
                    canvas.drawBitmap(bitmap, null, bigRect, null);
                    canvas.drawBitmap(BitmapManager.getBitmap(R.drawable.cross), null, POSITION, null);
                    drawPlayers(canvas, bigRect, miniMapTileSizeBig);
                } else {
                    canvas.drawBitmap(bitmap, null, POSITION, paintMiniMapSmall);
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
