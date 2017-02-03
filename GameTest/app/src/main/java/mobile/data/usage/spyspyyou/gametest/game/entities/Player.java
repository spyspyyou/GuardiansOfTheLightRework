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
import mobile.data.usage.spyspyyou.gametest.utils.paints.ColorPaint;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.COLOR_VALUE_ALLY;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.COLOR_VALUE_BAR_BACKGROUND;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.COLOR_VALUE_ENEMY;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.COLOR_VALUE_USER;


public class Player extends Entity {

    protected boolean
            slimy = false,
            teamBlue = false;

    public final String ADDRESS;

    public final byte CHARACTER;

    private final Bitmap RAW_BITMAP;

    protected HUD hud;

    private Matrix rotationMatrix = new Matrix();

    public Player(boolean teamBlue, String address, byte characterId) {
        super(GameWorld.getSpawn(teamBlue), IdLinker.getBitmapId(characterId));
        this.teamBlue = teamBlue;
        ADDRESS = address;
        CHARACTER = characterId;
        RAW_BITMAP = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(GameActivity.getRec(), IdLinker.getBitmapId(characterId)), width, height, false);
        hud = new HUD(this instanceof User);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (visible) {
            hud.render(canvas);
        }
    }

    @Override
    public void update(Game game){}

    public boolean isTeamBlue(){
        return teamBlue;
    }

    public float getRadius(){
        return width / 2 / SurfaceViewGame.getTileSide();
    }

    protected void setRadius(int radius){
        width = height = 2*radius;
    }

    protected void setDirection(double direction){
        if (direction != direction){
            Log.w("Entity", "direction is NaN");
            return;
        }
        long startTime = System.nanoTime();

        //reset all
        rotationMatrix.reset();
        rotationMatrix.postRotate((float) (direction / Math.PI * 360 / 2) - 90);
        if (direction == 0){
            bitmap = RAW_BITMAP;
            return;
        }
        //rotate
        bitmap = Bitmap.createBitmap(RAW_BITMAP, 0, 0, RAW_BITMAP.getWidth(), RAW_BITMAP.getHeight(), rotationMatrix, false);
        Log.d("Game-update", "setDirection took " + (System.nanoTime() - startTime) + " nanos, " + (int) ((System.nanoTime() - startTime) / 1000000) + " milis");
        if ((System.nanoTime() - startTime) > 3000000)Log.d("Game-update", "direction = " + direction);
    }

    public boolean isBlue(){
        return teamBlue;
    }

    protected class HUD {

        private static final int margin = 10;

        private Paint
                barBackgroundColor = new ColorPaint(COLOR_VALUE_BAR_BACKGROUND),
                healthBarColor = new ColorPaint(COLOR_VALUE_ENEMY);

        public HUD(boolean user){
            if (user) healthBarColor.setColor(COLOR_VALUE_USER);
            else if (teamBlue)healthBarColor.setColor(COLOR_VALUE_ALLY);
        }

        public void render(Canvas canvas){

        }
    }
}
