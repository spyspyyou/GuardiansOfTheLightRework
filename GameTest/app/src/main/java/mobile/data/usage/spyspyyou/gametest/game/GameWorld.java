package mobile.data.usage.spyspyyou.gametest.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.util.Pair;

import mobile.data.usage.spyspyyou.gametest.R;
import mobile.data.usage.spyspyyou.gametest.ui.GameActivity;
import mobile.data.usage.spyspyyou.gametest.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

import static mobile.data.usage.spyspyyou.gametest.game.WorldVars.LIGHT_BULB_STAND;
import static mobile.data.usage.spyspyyou.gametest.game.WorldVars.VOID;

public class GameWorld implements WorldVars{

    private static final int[][] DRAWABLE_RESOURCES = {
            {R.drawable.void_tile},
            {R.drawable.floor_tile},
            {R.drawable.wall_tile},
            {R.drawable.spawn_tile},
            {R.drawable.light_bulb_tile}
    };

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

    private Bitmap currentBitmap;
    private Paint p = new Paint();

    public GameWorld(World world, Vector2D userPosition) {
        this.userPosition = userPosition;
        p.setAntiAlias(false);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        Tile[][] tiles = new Tile[LIGHT_BULB_STAND + 1][1];
        tiles[VOID][0] = voidTile = new Tile(VOID, (byte) 0, false, false);
        tiles[FLOOR][0] = new Tile(FLOOR, (byte) 0, true, false);
        tiles[WALL][0] = new Tile(WALL, (byte) 0, true, true);
        tiles[SPAWN][0] = new Tile(SPAWN, (byte) 0, true, false);
        tiles[LIGHT_BULB_STAND][0] = new Tile(LIGHT_BULB_STAND, (byte) 0, true, true);

        int size = (int) Math.sqrt(world.size());
        Log.d("World", "size " + size);
        map = new Tile[size][size];

        int x = 0, y = 0;
        for (Pair<Byte, Byte> aWorldData : world) {
            Log.d("World", "x:" + x + " y:" + y);
            map[x++][y] = tiles[aWorldData.first][aWorldData.second];
            if (x == size) {
                x = 0;
                ++y;
            }
        }
    }

    /*package*/ void render(Canvas canvas) {
        screenPosition = ;
        for () {
            for () {
                canvas.drawBitmap();
            }
        }
    }

    /*package*/ Tile getTile(Vector2D position){
        int
                x = position.getIntX(),
                y = position.getIntY();
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
            int tileSide = SurfaceViewGame.getTileSide();
            ID = id;
            META = meta;
            int recID = R.drawable.void_tile;
            if (ID < DRAWABLE_RESOURCES.length && META < DRAWABLE_RESOURCES[ID].length){
                recID = DRAWABLE_RESOURCES[ID][META];
            }
            BITMAP = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(GameActivity.getRec(), recID), tileSide, tileSide, false);
            SOLID = solid;
            IMPASSABLE = impassable;
        }
    }

}
