package mobile.data.usage.spyspyyou.gametest.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import mobile.data.usage.spyspyyou.gametest.R;
import mobile.data.usage.spyspyyou.gametest.ui.GameActivity;
import mobile.data.usage.spyspyyou.gametest.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.HALF_TILES_IN_HEIGHT;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.HALF_TILES_IN_WIDTH;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.MAX_TILES_IN_HEIGHT;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.MAX_TILES_IN_WIDTH;

public class GameWorld implements WorldVars{

    private static final int[][] DRAWABLE_RESOURCES = {
            {R.drawable.void_tile},
            {R.drawable.floor_tile},
            {R.drawable.wall_tile},
            {R.drawable.spawn_tile},
            {R.drawable.light_bulb_tile}
    };


    private int
            tileStartX,
            tileStartY;

    private double startX;

    private static Vector2D
            spawnBlue = new Vector2D(0, 0),
            spawnGreen = new Vector2D(0, 0),
            lightBulbStandBlue = new Vector2D(0, 0),
            lightBulbStandGreen = new Vector2D(0, 0);

    private Vector2D
            screenPosition = new Vector2D(0, 0),
            userPosition = new Vector2D(0, 0);

    private Tile[][] map;
    private Tile voidTile;

    private Paint paint = new Paint();
    private final int
            TILE_SIDE;

    private int totalTime = 0, count = 0;

    public GameWorld(World world, Vector2D userPosition) {
        this.userPosition = userPosition;
        startX = tileStartX = tileStartY = 0;
        TILE_SIDE = SurfaceViewGame.getTileSide();
        paint.setAntiAlias(false);

        Tile[][] tiles = new Tile[LIGHT_BULB_STAND + 1][1];
        tiles[VOID][0] = voidTile = new Tile(VOID, (byte) 0, false, false);
        tiles[FLOOR][0] = new Tile(FLOOR, (byte) 0, true, false);
        tiles[WALL][0] = new Tile(WALL, (byte) 0, true, true);
        tiles[SPAWN][0] = new Tile(SPAWN, (byte) 0, true, false);
        tiles[LIGHT_BULB_STAND][0] = new Tile(LIGHT_BULB_STAND, (byte) 0, true, true);

        int size = world.data.length;
        Log.d("World", "size " + size);
        map = new Tile[size][size];

        for (int y = 0; y < size; ++y){
            for (int x = 0; x < size; ++x){
                map[x][y] = tiles[world.data[x][y].first][world.data[x][y].second];
            }
        }
    }

    /*package*/ void render(Canvas canvas) {
        startX = SurfaceViewGame.getCenter().x - (HALF_TILES_IN_WIDTH + userPosition.x - userPosition.getIntX()) * TILE_SIDE;
        screenPosition.set(startX, SurfaceViewGame.getCenter().y - (HALF_TILES_IN_HEIGHT + userPosition.y - userPosition.getIntY())* TILE_SIDE);
        tileStartX = userPosition.getIntX() - HALF_TILES_IN_WIDTH;
        tileStartY  = userPosition.getIntY() - HALF_TILES_IN_HEIGHT;

        long startTime = System.nanoTime();
        for (int y = 0; y <= MAX_TILES_IN_HEIGHT + 1; ++y) {
            screenPosition.set(startX, screenPosition.y);

            for (int x = 0; x <= MAX_TILES_IN_WIDTH + 1; ++x) {
                canvas.drawBitmap(getTile(tileStartX + x,tileStartY + y).BITMAP, screenPosition.getFloatX(), screenPosition.getFloatY(), paint);
                screenPosition.add(TILE_SIDE, 0);
            }
            screenPosition.add(0, TILE_SIDE);
        }
        long time = (System.nanoTime() - startTime);
        ++count;
        totalTime += time;
        Log.d("GameWorld-render", "took " + time + " nanos, " + (int)(time / 1000000) + " milis" + "\naverage time " + (time / count / 1000000) + " milis");

    }

    private Tile getTile(Vector2D position){
        return getTile(position.getIntX(), position.getIntY());
    }

    /*package*/ Tile getTile(int x, int y){
        if (x < 0 || y < 0 || x >= map.length || y >= map[0].length) return voidTile;
        return map[x][y];
    }


    public Vector2D getLightBulbStandBlue() {
        return lightBulbStandBlue;
    }

    public Vector2D getLightBulbStandGreen() {
        return lightBulbStandGreen;
    }

    public static Vector2D getSpawn(boolean teamBlue) {
        if (teamBlue) return spawnBlue;
        else return spawnGreen;
    }

    public boolean isSolid(Vector2D position) {
        return getTile(position).SOLID;
    }

    public boolean isImpassable(Vector2D position) {
        return getTile(position).IMPASSABLE;

    }

    /*package*/ class Tile {

        protected final Bitmap
                BITMAP;

        protected final byte
                ID,
                META;

        protected final boolean
                SOLID,
                IMPASSABLE;

        protected Tile(byte id, byte meta, boolean solid, boolean impassable){
            int tileSide = SurfaceViewGame.getTileSide() + 1;
            ID = id;
            META = meta;
            int recID = R.drawable.void_tile;
            if (ID < DRAWABLE_RESOURCES.length && META < DRAWABLE_RESOURCES[ID].length){
                recID = DRAWABLE_RESOURCES[ID][META];
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BITMAP = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(GameActivity.getRec(), recID, options), tileSide, tileSide, false);
            Log.e("Tile", "bitmap config" + BITMAP.getConfig());
            SOLID = solid;
            IMPASSABLE = impassable;
        }
    }

}
