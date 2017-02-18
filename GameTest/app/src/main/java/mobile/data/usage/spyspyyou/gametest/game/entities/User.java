package mobile.data.usage.spyspyyou.gametest.game.entities;

import android.graphics.Canvas;
import android.util.Log;

import mobile.data.usage.spyspyyou.gametest.game.Game;
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

        //checkFloor(game);
        addMana();
        if (mana > MAX_MANA)mana = MAX_MANA;
    }

    private void move(){
        double newDirection = SurfaceViewGame.getUserDirection();
        if (direction != newDirection) {
            setDirection(newDirection);
        }

        position.add(VELOCITY.getVelocity(slimy));
    }

    private void checkFloor(Game game){
        float tileRad = (1f*getRadius())/SurfaceViewGame.getTileSide();
        Vector2D pointing = new Vector2D((int) (position.x - tileRad * FLOOR_CHECK_RATIO), (int) (position.y - tileRad * FLOOR_CHECK_RATIO));
        String s = "";
        boolean b = true;
        for (; pointing.y < position.y + tileRad * FLOOR_CHECK_RATIO + 1; ++pointing.y){
            for (pointing.x = (int) (position.x - tileRad * FLOOR_CHECK_RATIO); pointing.x < position.x + tileRad * FLOOR_CHECK_RATIO + 1; ++pointing.x){
                s+= "fall check on x" + pointing.x + "y" + pointing.y + '\n';
                if (game.getWorld().isSolid(pointing))b = false;
            }
        }
        Log.i("User", s);
        falling = b;
    }

    private void fallingUpdate(){
        setRadius(--width/2);
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
