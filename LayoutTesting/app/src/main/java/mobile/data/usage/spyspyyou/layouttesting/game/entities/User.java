package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Canvas;

import mobile.data.usage.spyspyyou.layouttesting.game.GameUIManager;
import mobile.data.usage.spyspyyou.layouttesting.game.VelocityVector2D;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public abstract class User extends Player {

    private static final int MAX_MANA = 1000;

    private final GameUIManager gameUIManager;
    private VelocityVector2D velocity;

    private int mana = 0;

    protected User(Vector2D entityPosition, int size, byte characterType, GameUIManager mGameUIManager) {
        super(entityPosition, size, characterType);
        gameUIManager = mGameUIManager;
        velocity = mGameUIManager.getUserVelocity();
    }


    @Override
    public void update() {
        //update position + direction
        move();
    }

    private void move(){
        if (gameUIManager.activeUserDirection())setDirection(gameUIManager.getUserDirection());

        position.add(velocity.getVelocity(slimy));
        //todo:hitbox
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (!visible) hud.render(canvas);
    }

    public abstract void activateSkill();
}
