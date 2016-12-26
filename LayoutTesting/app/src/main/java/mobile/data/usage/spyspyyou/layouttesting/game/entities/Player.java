package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Canvas;

import mobile.data.usage.spyspyyou.layouttesting.game.Tick;
import mobile.data.usage.spyspyyou.layouttesting.game.Vector2D;


public class Player extends Entity {

    protected boolean slimy = false;
    protected HUD hud;

    public Player(Vector2D entityPosition, boolean isVisible, byte characterType) {
        super(entityPosition, isVisible, Tick.ICON_FLUFFY);
    }

    protected Player(Vector2D entityPosition, boolean isVisible, int entityBitmapID) {
        super(entityPosition, isVisible, entityBitmapID);
    }


    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (visible){
            //hud.render(canvas);
        }
    }

    public void update(){

    }

    protected class HUD {

        public void render(Canvas canvas){

        }
    }
}
