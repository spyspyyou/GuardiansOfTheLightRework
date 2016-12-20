package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import mobile.data.usage.spyspyyou.layouttesting.game.BitmapManager;

import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ICON_FLUFFY;

public class Player extends Entity {

    private static Bitmap slimyOverlay;

    protected boolean slimy = false;

    public Player(int entityX, int entityY, boolean isVisible, byte type) {
        super(entityX, entityY, isVisible, BitmapManager.getBitmap(ICON_FLUFFY));
        slimyOverlay = BitmapManager.getBitmap(100);
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
