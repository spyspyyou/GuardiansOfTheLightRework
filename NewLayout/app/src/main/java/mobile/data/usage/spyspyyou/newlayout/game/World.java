package mobile.data.usage.spyspyyou.newlayout.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Pair;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.ui.activity.GotLActivity;

public class World implements WorldVars {

    public static final int
            MIN_SIZE = 22,
            MAX_SIZE = 70;

    //for every tile there are two numbers between 0 and 9
    //weather the id nor the meta may have more than one digit
    //the first one indicates the tile, the second one is additional information, ex. team or wall color etc...

    public Pair<Byte, Byte>[][] data;

    private final int SIZE;

    public World(String dataString) {
        SIZE = (int) Math.sqrt(dataString.length() / 2);
        data = new Pair[SIZE][SIZE];

        for (int y = 0; y < SIZE; ++y){
            for (int x = 0; x < SIZE; ++x){
                data[x][y] = new Pair<>(Byte.parseByte(dataString.substring(0, 1)), Byte.parseByte(dataString.substring(1, 2)));
                dataString = dataString.substring(2);
            }
        }
    }

    @Override
    public String toString(){
        String string = "";
        for (int y = 0; y < SIZE; ++y){
            for (int x = 0; x < SIZE; ++x){
                string += data[x][y].first + data[x][y].second;
            }
        }
        return string;
    }

    public Bitmap getBitmapRepresentation(){
        int bitmapSize = (int) GotLActivity.getRec().getDimension(R.dimen.world_size);
        Bitmap worldRepresentation = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
        Canvas drawer = new Canvas(worldRepresentation);
        int tileSize = worldRepresentation.getWidth() / SIZE;
        Bitmap[][] tileBitmaps = new Bitmap[DRAWABLE_RESOURCES.length][DRAWABLE_RESOURCES[0].length];
        for (int j = 0; j < DRAWABLE_RESOURCES[0].length; ++j){
            for (int i = 0; i < DRAWABLE_RESOURCES.length; ++i){
                tileBitmaps[i][j] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(GotLActivity.getRec(), DRAWABLE_RESOURCES[i][j]), tileSize, tileSize, false);
            }
        }

        for (int y = 0; y < SIZE; ++y){
            for (int x = 0; x < SIZE; ++x){
                drawer.drawBitmap(tileBitmaps[data[x][y].first][data[x][y].second], x * tileSize, y * tileSize, null);
            }
        }
        return worldRepresentation;
    }
}
