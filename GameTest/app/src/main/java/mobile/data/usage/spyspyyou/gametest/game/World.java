package mobile.data.usage.spyspyyou.gametest.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;

import mobile.data.usage.spyspyyou.gametest.R;
import mobile.data.usage.spyspyyou.gametest.ui.GameActivity;
import mobile.data.usage.spyspyyou.gametest.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;


public class World {
    //for every tile there are two numbers between 0 and 9
    //weather the id nor the meta may have more than one digit
    //the first one indicates the tile, the second one is additional information, ex. team or wall color etc...

    //tile codes
    private static final byte
            VOID = 0,
            FLOOR = 1,
            WALL = 2,
            SPAWN = 3,
            LIGHT_BULB_STAND = 4;

    private static final int[][] DRAWABLE_RESOURCES = {
            {R.drawable.void_tile},
            {R.drawable.floor_tile},
            {R.drawable.wall_tile},
            {R.drawable.spawn_tile},
            {R.drawable.light_bulb_tile}
    };

    private static final int[] PIXEL_COLORS = {
            Color.BLACK,
            Color.WHITE,
            Color.RED,
            Color.GRAY,
            Color.YELLOW
    };

    private static final byte
            BLUE = 0,
            GREEN = 1;

    private Tile[][] map;
    private Tile voidTile;

    public World(String dataString) {
        Tile[][] tiles = new Tile[LIGHT_BULB_STAND][0];
        tiles[VOID][0] = voidTile = new Tile(VOID, (byte) 0, false, false);
        tiles[FLOOR][0] = new Tile(FLOOR, (byte) 0, true, false);
        tiles[WALL][0] = new Tile(WALL, (byte) 0, true, true);
        tiles[SPAWN][0] = new Tile(SPAWN, (byte) 0, true, false);
        tiles[LIGHT_BULB_STAND][0] = new Tile(LIGHT_BULB_STAND, (byte) 0, true, true);

        ArrayList<Pair<Byte, Byte>> worldData = new ArrayList<>();

        for (int i = 0; i < dataString.length() - 1; ) {
            worldData.add(new Pair<>(Byte.parseByte(dataString.substring(i, ++i)), Byte.parseByte(dataString.substring(i, ++i))));
        }

        int size = (int) Math.sqrt(worldData.size() / 2);
        map = new Tile[size][size];

        int x = 0, y = 0;
        Iterator<Pair<Byte, Byte>> iterator = worldData.iterator();
        while (iterator.hasNext()) {
            Pair<Byte, Byte> pair = iterator.next();
            map[x++][y] = tiles[pair.first][pair.second];
            if (x == size) {
                x = 0;
                ++y;
            }
        }
    }

    @Override
    public String toString(){
        String data = "";
        for (Tile[]tiles:map){
            for (Tile tile:tiles){
                data += tile.ID + tile.META;
            }
        }
        return data;
    }

    /*package*/ Tile getTile(Vector2D position){
        int
                x = position.getIntX(),
                y = position.getIntY();
        if (x < 0 || y < 0 || x >= map.length || y >= map[0].length) return voidTile;
        return map[x][y];
    }

    public Bitmap getBitmapRepresentation(){
        Bitmap pixelMap = Bitmap.createBitmap(map[0].length, map.length, Bitmap.Config.ARGB_8888);
        Canvas drawer = new Canvas(pixelMap);
        for (int y = 0; y < pixelMap.getHeight(); ++y){
            for (int x = 0; x < pixelMap.getWidth(); ++x){
                pixelMap.setPixel(x, y, PIXEL_COLORS[map[x][y].ID]);
            }
        }

        return pixelMap;
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
