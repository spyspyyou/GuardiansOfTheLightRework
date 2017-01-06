package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.layouttesting.utils.BitmapManager;
import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public abstract class Entity {

    // center of the entity
    protected Vector2D position;

    protected int width, height;

    private Matrix rotationMatrix = new Matrix();
    protected RectF rect = new RectF();
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

    protected Entity(Vector2D entityPosition, int  entityBitmapID){
        position = entityPosition;
        bitmapID = entityBitmapID;
        bitmap = BitmapManager.getBitmap(bitmapID);
        width = height = SurfaceViewGame.getTileSide();
    }

    public abstract void update(Game game);

    public void render(Canvas canvas){
        if(visible){
            Game.updateScreenPosition(position, screenPosition);

            rect.set(screenPosition.getIntX() - width / 2, screenPosition.getIntY() - height / 2, screenPosition.getIntX() + width / 2, screenPosition.getIntY() + height / 2);
            canvas.drawBitmap(bitmap, null, rect, null);
        }
    }

    protected void setDirection(double direction){
        if (direction != direction){
            Log.w("Entity", "direction is NaN");
            return;
        }

        //reset all
        rotationMatrix.reset();
        if (direction != direction)Log.i("Entity", "direction is NaN");
        rotationMatrix.postRotate((float) (direction / Math.PI * 360 / 2) - 90);
        bitmap = BitmapManager.getBitmap(bitmapID);

        if (direction == 0)return;

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
