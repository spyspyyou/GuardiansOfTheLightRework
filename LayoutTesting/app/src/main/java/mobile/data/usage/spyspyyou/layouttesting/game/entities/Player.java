package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Canvas;

import mobile.data.usage.spyspyyou.layouttesting.game.IdLinker;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;


public class Player extends Entity {

    protected boolean
            slimy = false,
            ally = false;

    protected HUD hud = new HUD();

    public Player(Vector2D entityPosition, int size, byte characterId) {
        super(entityPosition, size, size, IdLinker.getBitmapId(characterId));
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (visible){
            hud.render(canvas);
        }
    }

    public void update(){}

    public boolean isAlly(){
        return ally;
    }

    public int getRadius(){
        return width / 2;
    }

    protected class HUD {

        public void render(Canvas canvas){

        }
    }
}
