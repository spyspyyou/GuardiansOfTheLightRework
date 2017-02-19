package mobile.data.usage.spyspyyou.gametest.game.entities;

import android.graphics.Canvas;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.GameWorld;
import mobile.data.usage.spyspyyou.gametest.game.Tick;
import mobile.data.usage.spyspyyou.gametest.game.UserVelocityVector2D;
import mobile.data.usage.spyspyyou.gametest.game.events.server.GumClientEvent;
import mobile.data.usage.spyspyyou.gametest.teststuff.VARS;
import mobile.data.usage.spyspyyou.gametest.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;


public abstract class User extends Player {

    public static final int
            MAX_MANA = 1000;
    //number is seconds
    public static final int
            GUM_COOL_DOWN = (int) (0.5f * Tick.TICK);
    private static final float
            FLOOR_CHECK_RATIO = 0.75f;
    public static float
            ticksTillNextShot = 0;
    public static int
            MANA_USAGE;

    private double direction = -Math.PI / 2;

    protected final UserVelocityVector2D VELOCITY;

    public static float mana = 0;

    private boolean falling = false;

    protected User(boolean teamBlue, byte characterType, int abilityManaUsage) {
        super(teamBlue, VARS.address, characterType);
        userPosition = position;
        VELOCITY = SurfaceViewGame.getUserVelocity();
        MANA_USAGE = abilityManaUsage;
    }

    @Override
    public void update(Game game) {
        if (falling){
            fallingUpdate();
            return;
        }
        --ticksTillNextShot;

        //update position + direction
        move();

        checkFloor();
        addMana();
        if (mana > MAX_MANA)mana = MAX_MANA;
    }

    private void move(){
        double newDirection = SurfaceViewGame.getUserDirection();
        if (direction != newDirection) {
            setDirection(newDirection);
        }

        position.add(VELOCITY.getVelocity(slimy));
        hitBoxResolution();
    }

    private void hitBoxResolution(){
        Vector2D vector2D = new Vector2D(position.getIntX(), position.getIntY());
        vector2D.add(1, 0);
        if (GameWorld.isImpassable(vector2D) && position.x - position.getIntX() > 0.5){
            position.set(position.getIntX() + 0.5, position.y);
        }
        vector2D.add(-1, 1);
        if (GameWorld.isImpassable(vector2D) && position.y - position.getIntY() > 0.5){
            position.set(position.x, position.getIntY() + 0.5);
        }
        vector2D.add(-1, -1);
        if (GameWorld.isImpassable(vector2D) && position.x - position.getIntX() < 0.5){
            position.set(position.getIntX() + 0.5, position.y);
        }
        vector2D.add(1, -1);
        if (GameWorld.isImpassable(vector2D) && position.y - position.getIntY() < 0.5){
            position.set(position.x, position.getIntY() + 0.5);
        }

        Vector2D con;
        double d2;

        vector2D.set(position.getIntX(), position.getIntY());
        d2 = vector2D.squareDistance(position);
        vector2D.add(-1, -1);
        if (GameWorld.isImpassable(vector2D) && d2 < 0.5 * 0.5){
            vector2D.add(1, 1);
            con = new Vector2D(vector2D.x - position.x, vector2D.y - position.y);
            con.scaleTo(0.5);
            position.set(position.getIntX() + (1 - con.x) - 1, position.getIntY() + (1 - con.y) - 1);
        }

        vector2D.set(position.getIntX() + 1, position.getIntY());
        d2 = vector2D.squareDistance(position);
        vector2D.add(0, -1);
        if (GameWorld.isImpassable(vector2D) && d2 < 0.5 * 0.5){
            vector2D.add(0, 1);
            con = new Vector2D(vector2D.x - position.x, vector2D.y - position.y);
            con.scaleTo(0.5);
            position.set(position.getIntX() + (1 - con.x), position.getIntY() + (1 - con.y) - 1);
        }

        vector2D.set(position.getIntX(), position.getIntY() + 1);
        d2 = vector2D.squareDistance(position);
        vector2D.add(-1, 0);
        if (GameWorld.isImpassable(vector2D) && d2 < 0.5 * 0.5){
            vector2D.add(1, 0);
            con = new Vector2D(vector2D.x - position.x, vector2D.y - position.y);
            con.scaleTo(0.5);
            position.set(position.getIntX() + (1 - con.x) - 1, position.getIntY() + (1 - con.y));
        }

        vector2D.set(position.getIntX() + 1, position.getIntY() + 1);
        d2 = vector2D.squareDistance(position);
        if (GameWorld.isImpassable(vector2D) && d2 < 0.5 * 0.5){
            con = new Vector2D(vector2D.x - position.x, vector2D.y - position.y);
            con.scaleTo(0.5);
            position.set(position.getIntX() + (1 - con.x), position.getIntY() + (1 - con.y));
        }
    }

    private void checkFloor(){
        if (!GameWorld.isSolid(position))falling = true;
    }

    private void fallingUpdate(){
        height = --width;
        if (width <= 0){
            falling = false;
            width = height = SurfaceViewGame.getTileSide();
            position.set(GameWorld.getSpawn(teamBlue).x, GameWorld.getSpawn(teamBlue).y);
        }
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (!visible) hud.render(canvas);
    }

    public void shootGum(Game game){
        if (ticksTillNextShot <= 0){
            ticksTillNextShot = GUM_COOL_DOWN;
            new GumClientEvent(position, direction, game.getSynchronizedTick(), teamBlue).send();
        }
    }

    protected abstract void addMana();

    public boolean activateSkill(Game game){
        if (MANA_USAGE > mana)return false;
        mana -= MANA_USAGE;
        return true;
    }

    @Override
    protected void setDirection(double direction) {
        super.setDirection(direction);
        this.direction = direction;
    }
}
