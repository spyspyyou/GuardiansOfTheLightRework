package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Canvas;

import mobile.data.usage.spyspyyou.layouttesting.game.GameUIManager;
import mobile.data.usage.spyspyyou.layouttesting.game.Vector2D;

public abstract class User extends Player {

    private static final float SLIMY_SLOW = 0.2f;
    private static final int MAX_MANA = 1000;
    private final GameUIManager gameUIManager;
    private int mana;

    public User(Vector2D entityPosition, boolean isVisible, byte characterType, GameUIManager mGameUIManager) {
        super(entityPosition, isVisible, characterType);
        gameUIManager = mGameUIManager;
    }


    @Override
    public void update() {
        //update position + direction
        move();
    }

    private void move(){
        setDirection(gameUIManager.getUserDirection());

        Vector2D velocity = gameUIManager.getUserVelocity();
        if (slimy)velocity.scale(SLIMY_SLOW);
        position.add(velocity);
        //todo:hitbox
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (!visible) hud.render(canvas);
    }

    public abstract void activateSkill();
}
