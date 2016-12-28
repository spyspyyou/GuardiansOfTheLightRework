package mobile.data.usage.spyspyyou.layouttesting.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseArray;

import mobile.data.usage.spyspyyou.layouttesting.R;

import static android.graphics.BitmapFactory.decodeResource;

public class BitmapManager {

    private final static int BITMAP_RESOURCES[] = {
            R.drawable.blue_base_tile,
            R.drawable.floor_tile,
            R.drawable.green_base_tile,
            R.drawable.light_bulb_tile_team_blue,
            R.drawable.light_bulb_tile_team_green,
            R.drawable.spawn_tile,
            R.drawable.wall_tile,
            R.drawable.void_tile,
            R.drawable.fluffy,
            R.drawable.joystick_button_middle,
            R.drawable.joystick_button_ring,
            R.drawable.test_map_3
    };

    private static Bitmap defaultBitmap;
    private static int tileSize;
    private static boolean loaded = false;

    private static SparseArray<Bitmap> bitmaps = new SparseArray<>();

    /*package*/ static void loadBitmaps(Resources resources, int mTileSize){
        //TODO:the bitmaps should be loaded the size of a tile / their most likely size
        if (loaded){
            Log.i("BitmapManager", "Bitmaps already loaded");
            return;
        }

        Log.i("BitmapManager", "loading");

        loaded = true;
        tileSize = mTileSize;

        defaultBitmap = decodeResource(resources, R.mipmap.ic_launcher);
        for (int recID:BITMAP_RESOURCES){
            bitmaps.append(recID, loadBitmap(resources, recID));
        }
    }

    private static Bitmap loadBitmap(Resources resources, int recID){
        Bitmap raw = BitmapFactory.decodeResource(resources, recID);
        int width = raw.getWidth(), height = raw.getHeight();
        if (width >= height){
            width = tileSize;
            height *= tileSize;
            height /= raw.getWidth();
        }else{
            height = tileSize;
            width *= tileSize;
            width /= raw.getHeight();
        }
        return Bitmap.createScaledBitmap(raw, width, height, false);
    }

    public static void clearMemory(){
        loaded = false;
        bitmaps.removeAtRange(0, bitmaps.size() - 1);
    }

    public static Bitmap getBitmap(int recID){
        return bitmaps.get(recID, defaultBitmap);
    }
}
