package mobile.data.usage.spyspyyou.gametest.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

import static java.math.RoundingMode.FLOOR;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.HALF_TILES_IN_HEIGHT;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.HALF_TILES_IN_WIDTH;

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

        Game.updateScreenPosition(new Vector2D(userPosition.getIntX() - HALF_TILES_IN_WIDTH, userPosition.getIntY() - HALF_TILES_IN_HEIGHT), screenPosition);
        double startPositionX = screenPosition.x;

        for (int tileIndexY = userPosition.getIntY() - HALF_TILES_IN_HEIGHT; tileIndexY <= userPosition.getIntY() + HALF_TILES_IN_HEIGHT; ++tileIndexY){
            screenPosition.x = startPositionX;
            for (int tileIndexX = userPosition.getIntX() - HALF_TILES_IN_WIDTH; tileIndexX <= userPosition.getIntX() + HALF_TILES_IN_WIDTH; ++tileIndexX){
                bitmap = getTile(tileIndexX, tileIndexY).getBITMAP();

                rect.set(screenPosition.getIntX(), screenPosition.getIntY(), screenPosition.getIntX() + TILE_SIDE, screenPosition.getIntY() + TILE_SIDE);

                tmp = System.nanoTime();
                canvas.drawBitmap(bitmap, null, rect, p);
                long diff = System.nanoTime() - tmp;
                nanoCount += diff;
                if (diff>longestTime){
                    longestTime = diff;
                    longestTileType = tiles.keyAt(tiles.indexOfValue(getTile(tileIndexX, tileIndexY)));
                }

                screenPosition.x += TILE_SIDE;
            }
            screenPosition.y += TILE_SIDE;
        }

        if (nanoCount / 1000000 > 20) Log.e("GameMap.render", "took: " + nanoCount / 1000000 + " longestType: " + getTypeString(longestTileType));
        else Log.d("GameMap.render", "took: " + nanoCount / 1000000);
    }

    private String getTypeString(int type){
        switch (type){
            case VOID:return "VOID";
            case FLOOR:return "FLOOR";
            case WALL:return "WALL";
            case SPAWN_BLUE:return "SPAWN_BLUE";
            case SPAWN_GREEN:return "SPAWN_GREEN";
            case LIGHT_BULB_STAND_BLUE: return "LIGHT_BULB_STAND_BLUE";
            case LIGHT_BULB_STAND_GREEN: return "LIGHT_BULB_STAND_GREEN";
            default:return "nopes";
        }
    }

    public boolean isSolid(Vector2D position){
        return world.getTile(position).SOLID;
    }

    public boolean isImpassable(Vector2D position){
        return world.getTile(position).IMPASSABLE;

    }
}
