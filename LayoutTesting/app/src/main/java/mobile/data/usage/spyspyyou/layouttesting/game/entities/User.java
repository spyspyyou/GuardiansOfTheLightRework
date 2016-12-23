package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Canvas;

import mobile.data.usage.spyspyyou.layouttesting.game.GameUIManager;

public abstract class User extends Player {

    private static final float SLIMY_SLOW = 0.2f;
    private final GameUIManager gameUIManager;

    public User(int entityX, int entityY, boolean isVisible, byte type, GameUIManager mGameUIManager) {
        super(entityX, entityY, isVisible, type);
        gameUIManager = mGameUIManager;
    }

    @Override
    public void update() {
        //update position + direction
        direction = gameUIManager.getUserDirection();
        move();
    }

    private void move(){
        double
                xVelocity = gameUIManager.getUserVelocityX(),
                yVelocity = gameUIManager.getUserVelocityY();
        if (slimy){
            xVelocity *= SLIMY_SLOW;
            yVelocity *= SLIMY_SLOW;
        }
        x += xVelocity;
        y += yVelocity;
        //todo:hitbox
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
    }

    public void activateSkill(){

    }
}
