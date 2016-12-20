package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public abstract class Entity {

    protected float x, y;

    protected boolean visible = true;

    protected Bitmap bitmap;

    public Entity(int entityX, int entityY, boolean isVisible, Bitmap entityBitmap){
        x = entityX;
        y = entityY;
        visible = isVisible;
        bitmap = entityBitmap;
    }

    public abstract void update();

    public void render(Canvas canvas){
        if(visible){
            //todo:make get pos on screen method canvas.drawBitmap(bitmap, x, y, null);
        }
    }
}
