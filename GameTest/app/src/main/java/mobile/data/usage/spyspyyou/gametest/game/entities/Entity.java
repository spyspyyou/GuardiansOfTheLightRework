package mobile.data.usage.spyspyyou.gametest.game.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.ui.GameActivity;
import mobile.data.usage.spyspyyou.gametest.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;


public abstract class Entity {

    protected boolean visible = true;

    protected int width, height;

    // center of the entity
    protected Vector2D
            position,
            screenPosition;

    protected RectF rect = new RectF();
    protected Bitmap bitmap;

    protected Entity(Vector2D entityPosition, int width, int height, int  bitmapID){
        position = entityPosition;
        bitmap = BitmapFactory.decodeResource(GameActivity.getRec(), bitmapID);
        this.width = width;
        this.height = height;
    }

    protected Entity(Vector2D entityPosition, int  bitmapID){
        position = entityPosition;
        bitmap = BitmapFactory.decodeResource(GameActivity.getRec(), bitmapID);
        width = height = SurfaceViewGame.getTileSide();
    }

    public abstract void update(Game game);

    public void render(Canvas canvas, Vector2D userPosition){
        if(visible){
            updateScreenPosition(userPosition);
            rect.set(screenPosition.getIntX() - width / 2, screenPosition.getIntY() - height / 2, screenPosition.getIntX() + width / 2, screenPosition.getIntY() + height / 2);
            canvas.drawBitmap(bitmap, null, rect, null);
        }
    }

    public Vector2D getPosition(){
        return position;
    }

    protected void updateScreenPosition(Vector2D userPosition){
        screenPosition.set(
                SurfaceViewGame.getCenter().x + (position.x - userPosition.x) * SurfaceViewGame.getTileSide(),
                SurfaceViewGame.getCenter().y + (position.y - userPosition.y) * SurfaceViewGame.getTileSide()
        );
    }
}
