package mobile.data.usage.spyspyyou.layouttesting.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseArray;

import mobile.data.usage.spyspyyou.layouttesting.R;

public class BitmapManager {

    private static Bitmap defaultBitmap;
    private static boolean loaded = false;

    private static SparseArray<Bitmap> bitmaps = new SparseArray<>();

    /*package*/ static void loadBitmaps(Resources resources){
        if (loaded){
            Log.i("BitmapManager", "Bitmaps already loaded");
            return;
        }
        loaded = true;
        defaultBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
    }

    public static void clearMemory(){
        loaded = false;
        bitmaps.removeAtRange(0, bitmaps.size() - 1);
    }

    public static Bitmap getBitmap(int recID){
        return bitmaps.get(recID, defaultBitmap);
    }
}
