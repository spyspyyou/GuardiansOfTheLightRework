package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.BitmapManager;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public abstract class Entity {

    // center of the entity
    protected Vector2D position;

    private double direction = 0;

    private int width, height;

    protected boolean visible = true;
    private boolean bitmapRecalculationRequired = false;

    private final int bitmapID;
    private Bitmap bitmap;

    public Entity(Vector2D entityPosition, boolean isVisible, int  entityBitmapID){
        position = entityPosition;
        visible = isVisible;
        bitmapID = R.drawable.fluffy;
        bitmap = BitmapManager.getBitmap(bitmapID);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
    }

    public Entity(Vector2D entityPosition, int width, int height, boolean isVisible, int  entityBitmapID){
        position = entityPosition;
        this.width = width;
        this.height = height;
        visible = isVisible;
        bitmapID = entityBitmapID;
        bitmap = Bitmap.createScaledBitmap(BitmapManager.getBitmap(bitmapID), width, height, false);
    }

    public abstract void update();

    public void render(Canvas canvas){
        if(visible){
            if (bitmapRecalculationRequired)updateBitmap();
            canvas.drawBitmap(bitmap, canvas.getWidth() / 2 - bitmap.getWidth()/2, canvas.getHeight() / 2 - bitmap.getHeight() / 2, null);
            //todo:make get pos on screen method canvas.drawBitmap(bitmap, x, y, null);
        }
    }

    private void updateBitmap(){
        Matrix matrix = new Matrix();
        matrix.postRotate((float) (direction / Math.PI * 360 / 2) - 90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(BitmapManager.getBitmap(bitmapID), width, height, false);
        bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, width, height, matrix, false);
        bitmapRecalculationRequired = false;
    }

    public void setDirection(double direction) {
        this.direction = direction;
        bitmapRecalculationRequired = true;
    }

    public void setWidth(int width) {
        this.width = width;
        bitmapRecalculationRequired = true;
    }

    public void setHeight(int height) {
        this.height = height;
        bitmapRecalculationRequired = true;
    }

    public void setSize(int size){
        setWidth(size);
        setHeight(size);
    }

    public Vector2D getPosition(){
        return position;
    }
}
