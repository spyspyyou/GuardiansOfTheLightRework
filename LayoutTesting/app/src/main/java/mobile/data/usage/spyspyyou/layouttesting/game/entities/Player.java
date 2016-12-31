package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Canvas;
import android.graphics.Paint;

import mobile.data.usage.spyspyyou.layouttesting.game.IdLinker;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_ALLY;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_BAR_BACKGROUND;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_ENEMY;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_USER;


public class Player extends Entity {

    protected boolean
            slimy = false,
            ally = false;

    protected HUD hud;

    public Player(Vector2D entityPosition, int size, byte characterId) {
        super(entityPosition, size, size, IdLinker.getBitmapId(characterId));
        if (this instanceof User){
            hud = new HUD(this instanceof User);
        }else{
            hud = new HUD(false);
        }
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

        private static final int margin = 10;

        private Paint
                barBackgroundColor = new Paint(),
                healthBarColor = new Paint();

        public HUD(boolean isUser){
            barBackgroundColor.setColor(COLOR_VALUE_BAR_BACKGROUND);
            if (isUser)healthBarColor.setColor(COLOR_VALUE_USER);
            else if (ally)healthBarColor.setColor(COLOR_VALUE_ALLY);
            else healthBarColor.setColor(COLOR_VALUE_ENEMY);
        }

        public void render(Canvas canvas){

        }
    }
}
