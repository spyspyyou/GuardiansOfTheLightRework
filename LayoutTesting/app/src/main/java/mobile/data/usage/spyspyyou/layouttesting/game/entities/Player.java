package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Canvas;
import android.graphics.Paint;

import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.game.IdLinker;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_ALLY;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_BAR_BACKGROUND;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_ENEMY;


public class Player extends Entity {

    protected boolean
            slimy = false,
            ally = false;

    protected HUD hud;

    public Player(Vector2D entityPosition, byte characterId) {
        super(entityPosition, SurfaceViewGame.getTileSide(), SurfaceViewGame.getTileSide(), IdLinker.getBitmapId(characterId));
        hud = new HUD();
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (visible){
            hud.render(canvas);
        }
    }

    @Override
    public void update(Game game){}

    public boolean isAlly(){
        return ally;
    }

    public float getRadius(){
        return width / 2;
    }

    protected void setRadius(int radius){
        width = height = 2*radius;
    }

    protected class HUD {

        private static final int margin = 10;

        private Paint
                barBackgroundColor = new Paint(),
                healthBarColor = new Paint();

        public HUD(){
            barBackgroundColor.setColor(COLOR_VALUE_BAR_BACKGROUND);
            if (ally)healthBarColor.setColor(COLOR_VALUE_ALLY);
            else healthBarColor.setColor(COLOR_VALUE_ENEMY);
        }

        public void render(Canvas canvas){

        }
    }
}
