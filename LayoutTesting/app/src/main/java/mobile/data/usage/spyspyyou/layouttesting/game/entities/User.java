package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Canvas;

import mobile.data.usage.spyspyyou.layouttesting.game.VelocityVector2D;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public abstract class User extends Player {

    protected static final int MAX_MANA = 1000;
    private final int manaUsage;

    private final SurfaceViewGame surfaceViewGame;
    protected VelocityVector2D velocity;

    protected int mana = 0;

    protected User(Vector2D entityPosition, int size, byte characterType, SurfaceViewGame mSurfaceViewGame, int abilityManaUsage) {
        super(entityPosition, size, characterType);
        surfaceViewGame = mSurfaceViewGame;
        velocity = surfaceViewGame.getUserVelocity();
        manaUsage = abilityManaUsage;
    }


    @Override
    public void update() {
        //update position + direction
        move();
        addMana();
        if (mana > MAX_MANA)mana = MAX_MANA;
    }

    private void move(){
        setDirection(surfaceViewGame.getUserDirection());

        position.add(velocity.getVelocity(slimy));
        //todo:hitbox
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (!visible) hud.render(canvas);
    }

    protected abstract void addMana();

    public boolean activateSkill(){
        if (manaUsage > mana)return false;
        mana -= manaUsage;
        return true;
    }
}
