package mobile.data.usage.spyspyyou.gametest.game.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.GameWorld;
import mobile.data.usage.spyspyyou.gametest.game.IdLinker;
import mobile.data.usage.spyspyyou.gametest.ui.GameActivity;
import mobile.data.usage.spyspyyou.gametest.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;
import mobile.data.usage.spyspyyou.gametest.utils.paints.ColorPaint;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.COLOR_VALUE_ALLY;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.COLOR_VALUE_BAR_BACKGROUND;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.COLOR_VALUE_ENEMY;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.COLOR_VALUE_USER;


public class Player extends Entity {

    protected boolean
            slimy = false,
            ally = false;

    public final String ADDRESS;

    public final byte CHARACTER;

    private final Bitmap rawBitmap;

    protected HUD hud;

    private Matrix rotationMatrix = new Matrix();

    public Player(boolean teamBlue, boolean ally, String address, byte characterId) {
        super(GameWorld.getSpawn(teamBlue), SurfaceViewGame.getTileSide(), SurfaceViewGame.getTileSide(), IdLinker.getBitmapId(characterId));
        this.ally = ally;
        ADDRESS = address;
        CHARACTER = characterId;
        rawBitmap = BitmapFactory.decodeResource(GameActivity.getRec(), IdLinker.getBitmapId(characterId));
        hud = new HUD(this instanceof User);
    }

    @Override
    public void render(Canvas canvas, Vector2D userPosition) {
        super.render(canvas, userPosition);
        if (visible) {
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

    protected void setDirection(double direction){
        if (direction != direction){
            Log.w("Entity", "direction is NaN");
            return;
        }

        //reset all
        rotationMatrix.reset();
        rotationMatrix.postRotate((float) (direction / Math.PI * 360 / 2) - 90);
        if (direction == 0){
            bitmap = rawBitmap;
            return;
        }

        int initialSize = rawBitmap.getWidth();

        //rotate
        bitmap = Bitmap.createBitmap(rawBitmap, 0, 0, initialSize, initialSize, rotationMatrix, true);

        int
                halfDifferenceWidth = (bitmap.getWidth() - initialSize) / 2,
                halfDifferenceHeight = (bitmap.getHeight() - initialSize) / 2;

        //crop away the unnecessary
        bitmap = Bitmap.createBitmap(bitmap, halfDifferenceWidth, halfDifferenceHeight, initialSize, initialSize);
    }

    protected class HUD {

        private static final int margin = 10;

        private Paint
                barBackgroundColor = new ColorPaint(COLOR_VALUE_BAR_BACKGROUND),
                healthBarColor = new ColorPaint(COLOR_VALUE_ENEMY);

        public HUD(boolean user){
            if (user) healthBarColor.setColor(COLOR_VALUE_USER);
            else if (ally)healthBarColor.setColor(COLOR_VALUE_ALLY);
        }

        public void render(Canvas canvas){

        }
    }
}
