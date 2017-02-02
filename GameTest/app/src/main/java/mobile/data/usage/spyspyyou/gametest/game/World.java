package mobile.data.usage.spyspyyou.gametest.game;

import android.graphics.Bitmap;
import android.util.Pair;

public class World implements WorldVars{
    //for every tile there are two numbers between 0 and 9
    //weather the id nor the meta may have more than one digit
    //the first one indicates the tile, the second one is additional information, ex. team or wall color etc...

    private Pair<Byte, Byte>[][] data;

    private final int SIZE;

    public World(String dataString) {
        SIZE = (int) Math.sqrt(dataString.length() / 2);
        data = new Pair[SIZE][SIZE];

        for (Pair<Byte, Byte>[] pairArray:data){
            for (Pair<Byte, Byte> pair:pairArray){
                pair = new Pair<>(Byte.parseByte(dataString.substring(0, 1)), Byte.parseByte(dataString.substring(1, 2)));
            }
        }
    }

    @Override
    public String toString(){
        String string = "";
        for (Pair<Byte, Byte> pair:data){
            string += pair.first + pair.second;
        }
        return string;
    }

    public Bitmap getBitmapRepresentation(){
        Bitmap pixelMap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        for (int y = 0; y < pixelMap.getHeight(); ++y){
            for (int x = 0; x < pixelMap.getWidth(); ++x){
                pixelMap.setPixel(x, y, PIXEL_COLORS[map[x][y].ID]);
            }
        }

        return pixelMap;
    }
}
