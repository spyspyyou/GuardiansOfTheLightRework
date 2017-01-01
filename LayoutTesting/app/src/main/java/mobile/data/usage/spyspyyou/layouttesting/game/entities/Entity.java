package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.game.BitmapManager;
import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public abstract class Entity {

    // center of the entity
    protected Vector2D position;

    protected int width, height;

    private Matrix rotationMatrix = new Matrix();
    private Rect rect = new Rect();
    protected Vector2D screenPosition = new Vector2D(0, 0);

    protected boolean visible = true;

    private final int bitmapID;
    private Bitmap bitmap;

    protected Entity(Vector2D entityPosition, int width, int height, int  entityBitmapID){
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

    protected void setDirection(double direction){
        if (direction == 0)return;

        //reset all
        rotationMatrix.reset();
        if (direction != direction)Log.i("Entity", "direction is NaN");
        rotationMatrix.postRotate((float) (direction / Math.PI * 360 / 2) - 90);
        bitmap = BitmapManager.getBitmap(bitmapID);

        int
                initialWidth = bitmap.getWidth(),
                initialHeight = bitmap.getHeight();

        //rotate
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, initialWidth, initialHeight, rotationMatrix, true);

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
