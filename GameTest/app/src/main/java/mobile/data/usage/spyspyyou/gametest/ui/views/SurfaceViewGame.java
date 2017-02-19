package mobile.data.usage.spyspyyou.gametest.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.gametest.R;
import mobile.data.usage.spyspyyou.gametest.game.Tick;
import mobile.data.usage.spyspyyou.gametest.game.UserVelocityVector2D;
import mobile.data.usage.spyspyyou.gametest.game.World;
import mobile.data.usage.spyspyyou.gametest.game.entities.Player;
import mobile.data.usage.spyspyyou.gametest.game.entities.User;
import mobile.data.usage.spyspyyou.gametest.game.events.local.GumButtonClickedEvent;
import mobile.data.usage.spyspyyou.gametest.game.events.local.SkillActivationEvent;
import mobile.data.usage.spyspyyou.gametest.ui.GameActivity;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;
import mobile.data.usage.spyspyyou.gametest.utils.paints.BorderPaint;
import mobile.data.usage.spyspyyou.gametest.utils.paints.ColorPaint;
import mobile.data.usage.spyspyyou.gametest.utils.paints.FillPaint;
import mobile.data.usage.spyspyyou.gametest.utils.paints.HolePaint;
import mobile.data.usage.spyspyyou.gametest.utils.paints.SrcOutPaint;

import static android.graphics.Bitmap.createBitmap;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.COLOR_VALUE_ALLY;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.COLOR_VALUE_ENEMY;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.COLOR_VALUE_USER;

public class SurfaceViewGame extends SurfaceView implements SurfaceHolder.Callback{

    public static final byte
            LEFT = 0,
            RIGHT = 1,
            RANDOM = 2;

    private int
            halfWidth = 0,
            halfHeight = 0,
            margin;

    private static int
            tileSide = 20;

    private boolean
            created = false,
            destroyed = false,
            setup = false,
            joyStickActive = false;

    private static double
            userDirection = 0;

    private static Vector2D
            screenCenter;

    private static UserVelocityVector2D
            userVelocity = new UserVelocityVector2D(0, 0);

    private SurfaceJoystick surfaceJoystick;

    private ArrayList<VirtualButton>
            buttons = new ArrayList<>();
    public SurfaceViewGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        margin = (int) getResources().getDimension(R.dimen.activity_margin);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        invalidate();
        halfWidth = getWidth() / 2;
        halfHeight = getHeight() / 2;
        screenCenter = new Vector2D(halfWidth, halfHeight);

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
        destroyed = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!setup)return false;
        for (VirtualButton button:buttons)button.onTouchEvent(event);

        if (isActionDown(event))surfaceJoystick.claimTouchSeries(event);
        surfaceJoystick.onTouchEvent(event);
        if (isActionUp(event))surfaceJoystick.releaseTouchSeries(event);

        return true;
    }

    public void setup(int controlType, World world, Player[] players){
        switch(controlType){
            case LEFT:
                createLeft(world, players);
                break;
            case RIGHT:
                createRight(world, players);
                break;
            case RANDOM:
            default:
                createRandom(world, players);
        }
        setup = true;
    }

    private void createLeft(World world, Player[] players){
        int miniMapSize = (int) getResources().getDimension(R.dimen.mini_map_size);
        int buttonSize = (int) getResources().getDimension(R.dimen.button_size);
        surfaceJoystick = new SurfaceJoystick(new Rect(0, 0, (int) getResources().getDimension(R.dimen.joystick_surface_width), getHeight()));
        buttons.add(new SurfaceMiniMap(new Rect(getWidth() - margin - miniMapSize, margin, getWidth() - margin, margin + miniMapSize), world, players));
        buttons.add(new SkillButton(new Rect(getWidth() - margin - buttonSize, getHeight() - margin - buttonSize, getWidth() - margin, getHeight() - margin)));
        buttons.add(new GumButton(new Rect(getWidth() - margin - buttonSize, getHeight() - 2*margin - 2*buttonSize, getWidth() - margin, getHeight() - 2*margin - buttonSize)));
    }

    private void createRight(World world, Player[] players){
        int miniMapSize = (int) getResources().getDimension(R.dimen.mini_map_size);
        surfaceJoystick = new SurfaceJoystick(new Rect(0, 0, (int) getResources().getDimension(R.dimen.joystick_surface_width), getHeight()));
        buttons.add(new SurfaceMiniMap(new Rect(getWidth() - margin - miniMapSize, margin, getWidth() - margin, margin + miniMapSize), world, players));
        //buttons.add(new FillingButton(new Rect(0, 0, 0, 0)));
        //buttons.add(new FillingButton(new Rect(0, 0, 0, 0)));
    }

    private void createRandom(World world, Player[] players){
        int miniMapSize = (int) getResources().getDimension(R.dimen.mini_map_size);
        surfaceJoystick = new SurfaceJoystick(new Rect(0, 0, (int) getResources().getDimension(R.dimen.joystick_surface_width), getHeight()));
        buttons.add(new SurfaceMiniMap(new Rect(getWidth() - margin - miniMapSize, margin, getWidth() - margin, margin + miniMapSize), world, players));
        //buttons.add(new FillingButton(new Rect(0, 0, 0, 0)));
        //buttons.add(new FillingButton(new Rect(0, 0, 0, 0)));
    }

    public void update(){
        surfaceJoystick.updateData();
    }

    public Canvas startDrawing() {
        try {
            return getHolder().lockCanvas();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void render(Canvas canvas) {
        surfaceJoystick.render(canvas);
        for (VirtualButton button : buttons) {
            button.render(canvas);
        }
    }

    public void finishDrawing(Canvas b){
        if (destroyed)return;
        try {
            getHolder().unlockCanvasAndPost(b);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static double getUserDirection() {
        return userDirection;
    }

    public static UserVelocityVector2D getUserVelocity(){
        return userVelocity;
    }

    public boolean isCreated() {
        return created;
    }

    public static Vector2D getCenter(){
        return screenCenter;
    }

    public static int getTileSide(){
        return tileSide;
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
            paintJoystickCenter.setAntiAlias(false);
            paintJoystickRing.setAntiAlias(false);
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
            pointerID = -1;
        }

        protected boolean onTouchEvent(MotionEvent event) {
            if (!joyStickActive)return false;
            dataChanged = true;

            fingerDisplacement.set(event.getX(event.findPointerIndex(pointerID)) - joystickPosition.x, event.getY(event.findPointerIndex(pointerID)) - joystickPosition.y);
            return true;
        }

        public void render(Canvas canvas) {
            if (joyStickActive) {
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
                bitmap,
                cross;

        private SurfaceMiniMap(Rect position, World world, Player[] mPlayers) {
            super(position);
            size = (int) getResources().getDimension(R.dimen.mini_map_size);

            bitmap = world.getBitmapRepresentation();
            cross = BitmapFactory.decodeResource(GameActivity.getRec(), R.drawable.cross);
            players = mPlayers;

            bigRect = new Rect(halfWidth - getHeight() / 2 + margin, margin, halfWidth + getHeight() / 2 - margin, getHeight() - margin);

            miniMapTileSizeSmall = (1f * size) / bitmap.getWidth();
            miniMapTileSizeBig = (1f * (getHeight() - 2 * margin)) / bitmap.getWidth();

            paintMiniMapSmall.setAlpha(0xaa);
            paintMiniMapSmall.setAntiAlias(false);
            colorUser.setAntiAlias(false);
            colorAlly.setAntiAlias(false);
            colorEnemy.setAntiAlias(false);
        }

        public void render(Canvas canvas) {
            long startTime = System.nanoTime();
            if (canvas != null) {
                if (big) {
                    canvas.drawBitmap(bitmap, null, bigRect, null);
                    canvas.drawBitmap(cross, null, POSITION, null);
                    drawPlayers(canvas, bigRect, miniMapTileSizeBig);
                } else {
                    canvas.drawBitmap(bitmap, null, POSITION, paintMiniMapSmall);
                    drawPlayers(canvas, POSITION, miniMapTileSizeSmall);
                }
            }
            Log.d("SVG-render", "MiniMap took " + (System.nanoTime() - startTime) + " nanos, " + (int)((System.nanoTime() - startTime) / 1000000) + " milis");
        }

        private void drawPlayers(Canvas canvas, Rect rect, float miniMapTileSide){
            Paint currentColor;
            for (Player player:players){
                if (player instanceof User){
                    currentColor = colorUser;
                } else if (player.isTeamBlue()){
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

    private abstract class FillingButton extends VirtualButton{

        protected final float MAX_LEVEL = 1000;
        protected int level = 400;
        private static final float DARKEN = 0.8f;

        private Rect
                rect;
        private Bitmap
                button;

        protected Canvas
                bitmapEditor;
        private Paint
                paintFill = new FillPaint(Color.MAGENTA),
                paintSrcOut,
                paintSrcOutReady,
                paintStroke = new BorderPaint(6, Color.BLACK),
                holePaint = new HolePaint();


        private FillingButton(Rect position, int fillColor) {
            super(position);
            button = createBitmap(POSITION.width(),POSITION.height(), Bitmap.Config.ARGB_8888);
            bitmapEditor = new Canvas(button);
            rect = new Rect(0, 0, POSITION.width(), POSITION.height());
            paintSrcOutReady = new SrcOutPaint(fillColor);
            paintSrcOut = new SrcOutPaint(Color.rgb((int) (Color.red(fillColor) * DARKEN), (int) (Color.green(fillColor) * DARKEN), (int) (Color.blue(fillColor) * DARKEN)));
        }

        @Override
        protected void render(Canvas canvas) {
            long startTime = System.nanoTime();
            updateFillLevel();
            rect.set(0, (int) (POSITION.height() / MAX_LEVEL * (MAX_LEVEL-level)), POSITION.width(), POSITION.height());

            bitmapEditor.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            bitmapEditor.drawRect(rect, paintFill);
            bitmapEditor.drawCircle(POSITION.width() / 2, POSITION.height() / 2, POSITION.width() / 2 - 2, holePaint);
            if (isReady()){
                bitmapEditor.drawRect(rect, paintSrcOutReady);
            }else{
                bitmapEditor.drawRect(rect, paintSrcOut);
            }

            bitmapEditor.drawCircle(POSITION.width() / 2, POSITION.height() / 2, POSITION.width() / 2 - 4, paintStroke);
            canvas.drawBitmap(button, null, POSITION, null);
            Log.d("SVG-render", "FillButton took " + (System.nanoTime() - startTime) + " nanos, " + (int)((System.nanoTime() - startTime) / 1000000) + " milis");
        }

        protected abstract void updateFillLevel();

        protected abstract boolean isReady();
    }

    private class SkillButton extends FillingButton{

        private final Bitmap ICON;
        private Rect bitmapRect;

        private SkillButton(Rect position) {
            super(position, Color.CYAN);
            ICON = BitmapFactory.decodeResource(GameActivity.getRec(), R.drawable.slime_trail);
            bitmapRect = new Rect(POSITION.width() / 4 + POSITION.left, POSITION.height() / 4 + POSITION.top, POSITION.right - POSITION.width() / 4, POSITION.bottom - POSITION.height() / 4);
        }

        @Override
        protected void render(Canvas canvas) {
            super.render(canvas);
            canvas.drawBitmap(ICON, null, bitmapRect, null);
        }

        @Override
        protected void onClick() {
            Log.d("SkillButton", "clicked");
            new SkillActivationEvent().send();
        }

        @Override
        protected void updateFillLevel() {
            level = (int) (MAX_LEVEL * (User.mana / User.MAX_MANA));
        }

        @Override
        protected boolean isReady() {
            return User.mana >= User.MANA_USAGE;
        }
    }

    private class GumButton extends FillingButton{

        private GumButton(Rect position) {
            super(position, Color.RED);
        }

        @Override
        protected void onClick() {
            Log.d("GumButton", "clicked");
            new GumButtonClickedEvent().send();
        }

        @Override
        protected void updateFillLevel() {
            level = (int) (MAX_LEVEL * ((User.GUM_COOL_DOWN - User.ticksTillNextShot) / User.GUM_COOL_DOWN));
        }

        @Override
        protected boolean isReady() {
            return level >= MAX_LEVEL;
        }
    }

}
