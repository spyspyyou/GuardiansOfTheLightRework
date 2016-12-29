package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import mobile.data.usage.spyspyyou.layouttesting.game.BitmapManager;
import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

import static android.graphics.Bitmap.createBitmap;

public abstract class Entity {

    // center of the entity
    protected Vector2D position;

    protected int width, height;

    private Matrix rotationMatrix = new Matrix();
    private Rect rect = new Rect();
    private Vector2D screenPosition = new Vector2D(0, 0);

    protected boolean visible = true;

    private final int bitmapID;
    private Bitmap bitmap;

    public Entity(Vector2D entityPosition, int width, int height, int  entityBitmapID){
        position = entityPosition;
        bitmapID = entityBitmapID;
        bitmap = BitmapManager.getBitmap(bitmapID);
        this.width = width;
        this.height = height;
    }

    public abstract void update();

    public void render(Canvas canvas){
        if(visible){
            Game.updateScreenPosition(position, screenPosition);

            rect.set(screenPosition.getIntX() - width / 2, screenPosition.getIntY() - height / 2, screenPosition.getIntX() + width / 2, screenPosition.getIntY() + height / 2);
            canvas.drawBitmap(bitmap, null, rect, null);
        }
    }

    public void setDirection(double direction){

        //reset all
        rotationMatrix.reset();
        rotationMatrix.postRotate((float) (direction / Math.PI * 360 / 2) - 90);
        bitmap = BitmapManager.getBitmap(bitmapID);

        int
                initialWidth = bitmap.getWidth(),
                initialHeight = bitmap.getHeight();

        //rotate
        bitmap = createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotationMatrix, true);

        int
                halfDifferenceWidth = (bitmap.getWidth() - initialWidth) / 2,
                halfDifferenceHeight = (bitmap.getHeight() - initialHeight) / 2;

        //crop away the unnecessary
        bitmap = Bitmap.createBitmap(bitmap, halfDifferenceWidth, halfDifferenceHeight, initialWidth, initialHeight);
    }

    public Vector2D getPosition(){
        return position;
    }
}
