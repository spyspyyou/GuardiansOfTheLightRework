package mobile.data.usage.spyspyyou.gametest.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

public class GameWorld  {

    private Vector2D
            spawnBlue = new Vector2D(0, 0),
            spawnGreen = new Vector2D(0, 0),
            lightBulbStandBlue = new Vector2D(0, 0),
            lightBulbStandGreen = new Vector2D(0, 0);

    private World world;

    public GameWorld(World world){
        this.world = world;
    }

    /*package*/ void render(Canvas canvas, Vector2D userPosition){
        Bitmap bitmap;
        Paint p = new Paint();
        p.setAntiAlias(false);
        long nanoCount = 0;
        long tmp;
        long longestTime = 0;
        int longestTileType = 0;

        Log.d("GameMap.render", "took: " + nanoCount / 1000000);
    }

    public Vector2D getLightBulbStandBlue(){
        return lightBulbStandBlue;
    }

    public Vector2D getLightBulbStandGreen(){
        return lightBulbStandGreen;
    }

    public Vector2D getSpawnBlue() {
        return spawnBlue;
    }

    public Vector2D getSpawnGreen() {
        return spawnGreen;
    }

    public boolean isSolid(Vector2D position){
        return world.getTile(position).SOLID;
    }

    public boolean isImpassable(Vector2D position){
        return world.getTile(position).IMPASSABLE;

    }

}
