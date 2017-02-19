package mobile.data.usage.spyspyyou.gametest.game.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.ui.GameActivity;
import mobile.data.usage.spyspyyou.gametest.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;


public abstract class Entity {

    protected boolean visible = true;

    protected static Vector2D userPosition;
    protected Paint drawPaint = new Paint();

    protected int width, height;

    // center of the entity
    public Vector2D
            position,
            screenPosition = new Vector2D(0, 0);

    protected Bitmap bitmap;

    protected Entity(Vector2D entityPosition, int width, int height, int  bitmapID){
        position = entityPosition;
        bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(GameActivity.getRec(), bitmapID), width, height, false);
        this.width = width;
        this.height = height;
    }

    protected Entity(Vector2D entityPosition, int  bitmapID){
        position = entityPosition;
        width = height = SurfaceViewGame.getTileSide();
        bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(GameActivity.getRec(), bitmapID), width, height, false);
    }

    public abstract void update(Game game);

    public void render(Canvas canvas){
        if(visible){
            updateScreenPosition();
            canvas.drawBitmap(bitmap, (float) (screenPosition.x - bitmap.getWidth() / 2.0), (float) (screenPosition.y - bitmap.getHeight() / 2.0), drawPaint);
        }
    }

    public Vector2D getPosition(){
        return position;
    }

    protected void updateScreenPosition(){
        screenPosition.set(
                SurfaceViewGame.getCenter().x + (position.x - userPosition.x) * SurfaceViewGame.getTileSide(),
                SurfaceViewGame.getCenter().y + (position.y - userPosition.y) * SurfaceViewGame.getTileSide()
        );
    }
}
