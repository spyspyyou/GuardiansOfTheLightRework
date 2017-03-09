package mobile.data.usage.spyspyyou.newlayout.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.io.Serializable;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.ui.activity.GotLActivity;

public class World implements Serializable, WorldVars {

    public static final int
            MIN_SIZE = 22,
            MAX_SIZE = 70;

    protected byte[][] data;

    public World(byte[][] worldData) {
        data = worldData;
    }

    public Bitmap getBitmapRepresentation(){
        int bitmapSize = (int) GotLActivity.getRec().getDimension(R.dimen.world_size);
        Bitmap worldRepresentation = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
        Canvas drawer = new Canvas(worldRepresentation);
        int tileSize = worldRepresentation.getWidth() / data.length;

        Bitmap[] tileBitmaps = new Bitmap[DRAWABLE_RESOURCES.length];
        for (int i = 0; i < DRAWABLE_RESOURCES.length; ++i){
            tileBitmaps[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(GotLActivity.getRec(), DRAWABLE_RESOURCES[i]), tileSize, tileSize, false);
        }

        for (int y = 0; y < data[0].length; ++y){
            for (int x = 0; x < data.length; ++x){
                drawer.drawBitmap(tileBitmaps[data[x][y]], x * tileSize, y * tileSize, null);
            }
        }
        return worldRepresentation;
    }

    public byte[][] getData(){
        return data;
    }
}
