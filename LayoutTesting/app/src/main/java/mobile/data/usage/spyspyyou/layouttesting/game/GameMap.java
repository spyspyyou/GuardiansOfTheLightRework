package mobile.data.usage.spyspyyou.layouttesting.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public class GameMap {

    //color codes
    private static final int
            VOID = 0xff000000,
            FLOOR = 0xffffffff,
            WALL = 0xffff0000,
            SPAWN_BLUE = 0xff0000ff,
            SPAWN_GREEN = 0xff00ff00,
            LIGHT_BULB_STAND_BLUE = 0xff0088ff,
            LIGHT_BULB_STAND_GREEN = 0xff00ff88;


    private Vector2D
            spawnBlue = new Vector2D(0, 0),
            spawnGreen = new Vector2D(0, 0),
            lightBulbStandBlue = new Vector2D(0, 0),
            lightBulbStandGreen = new Vector2D(0, 0);

    private Tile[][] map;

    private SparseArray<Tile> tiles = new SparseArray<>();

    private final int TILE_SIDE;

    /*package*/ GameMap(GameUIManager gameUIManager, Bitmap pixelMap, int tileSide){
        TILE_SIDE = tileSide;
        //--------------------------------------------
        //load all tiles
        tiles.append(VOID, new Tile(BitmapManager.getBitmap(R.drawable.void_tile), false, false));

        tiles.append(FLOOR, new Tile(BitmapManager.getBitmap(R.drawable.floor_tile), true, false));

        tiles.append(WALL, new Tile(BitmapManager.getBitmap(R.drawable.wall_tile), true, true));

        Tile spawnTile = new Tile(BitmapManager.getBitmap(R.drawable.spawn_tile), true, false);
        tiles.append(SPAWN_BLUE, spawnTile);
        tiles.append(SPAWN_GREEN,spawnTile);

        tiles.append(LIGHT_BULB_STAND_BLUE, new Tile(BitmapManager.getBitmap(R.drawable.light_bulb_tile_team_blue), true, true));
        tiles.append(LIGHT_BULB_STAND_GREEN, new Tile(BitmapManager.getBitmap(R.drawable.light_bulb_tile_team_green), true, true));
        //--------------------------------------------
        readPixelMap(pixelMap);
        gameUIManager.setMap(pixelMap);
    }

    public void render(Canvas canvas, Vector2D userPosition){
        Bitmap bitmap;

        double startX = canvas.getWidth() / 2 - (Tick.MAX_TILES_IN_WIDTH / 2 + userPosition.x - userPosition.getIntX()) * TILE_SIDE;
        Vector2D screenPosition = new Vector2D(startX, canvas.getHeight() / 2 - (Tick.MAX_TILES_IN_HEIGHT / 2 + userPosition.y - userPosition.getIntY()) * TILE_SIDE);

        for (int y = userPosition.getIntY() - Tick.MAX_TILES_IN_HEIGHT / 2; y <= userPosition.getIntY() + Tick.MAX_TILES_IN_HEIGHT / 2; ++y){
            screenPosition.x = startX;
            for (int x = userPosition.getIntX() - Tick.MAX_TILES_IN_WIDTH / 2; x <= userPosition.getIntX() + Tick.MAX_TILES_IN_WIDTH / 2; ++x){
                bitmap = getTile(x, y).getBITMAP();

                canvas.drawBitmap(bitmap, screenPosition.getFloatX(), screenPosition.getFloatY(), null);
                screenPosition.x += TILE_SIDE;
            }
            screenPosition.y += TILE_SIDE;
        }
    }

    private Tile getTile(int x, int y){
        if (x < 0 || y < 0 || x >= map.length || y >= map[0].length) return tiles.get(VOID);
        return map[x][y];
    }

    private void readPixelMap(Bitmap pixelMap){
        map = new Tile[pixelMap.getWidth()][pixelMap.getHeight()];
        for (int y = 0; y < pixelMap.getHeight(); ++y){
            for (int x = 0; x < pixelMap.getWidth(); ++x){

                int pixel = pixelMap.getPixel(x, y);
                map[x][y] = tiles.get(pixel, tiles.get(VOID));
                if (tiles.get(pixel, tiles.get(VOID)) == null) Log.i("GameMap", "set tile x" + x + "y" + y + "to" + pixel);
                switch (pixel){
                    case SPAWN_BLUE:
                        spawnBlue = new Vector2D(x, y);
                        break;
                    case SPAWN_GREEN:
                        spawnGreen = new Vector2D(x, y);
                        break;
                    case LIGHT_BULB_STAND_BLUE:
                        lightBulbStandBlue = new Vector2D(x, y);
                        break;
                    case LIGHT_BULB_STAND_GREEN:
                        lightBulbStandGreen = new Vector2D(x, y);
                        break;
                }
            }
        }
    }

    public boolean iaSolid(Vector2D position){
        int
                x = position.getIntX(),
                y = position.getIntY();
        if (x < 0 || y < 0 || x >= map.length || y >= map[0].length) return false;
        return map[x][y].isSOLID();
    }

    public boolean isImpassable(Vector2D position){
        int
                x = position.getIntX(),
                y = position.getIntY();
        if (x < 0 || y < 0 || x >= map.length || y >= map[0].length) return false;
        return map[x][y].isIMPASSABLE();
    }

    public Vector2D getSpawnBlue() {
        return spawnBlue;
    }

    public Vector2D getSpawnGreen() {
        return spawnGreen;
    }

    public Vector2D getLightBulbStandBlue() {
        return lightBulbStandBlue;
    }

    public Vector2D getLightBulbStandGreen() {
        return lightBulbStandGreen;
    }

    private class Tile {

        private final Bitmap BITMAP;
        private final boolean
                SOLID,
                IMPASSABLE;

        public Tile(Bitmap bitmap, boolean solid, boolean impassable){
            BITMAP = bitmap;
            SOLID = solid;
            IMPASSABLE = impassable;
        }

        public Bitmap getBITMAP() {
            return BITMAP;
        }

        public boolean isSOLID() {
            return SOLID;
        }

        public boolean isIMPASSABLE() {
            return IMPASSABLE;
        }
    }
}
