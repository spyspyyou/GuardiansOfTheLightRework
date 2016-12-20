package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Canvas;

public class Player extends Entity {

    private byte playerType;

    protected boolean slimy = false;

    public Player(int entityX, int entityY, boolean isVisible, byte type) {
        super(entityX, entityY, isVisible, entityBitmap);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (slimy){
            //draw slimy overlay on the player
        }
    }

    public void update(){

    }
}
