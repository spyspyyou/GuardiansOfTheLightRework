package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.BitmapManager;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.Player;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.User;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_ALLY;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_ENEMY;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.COLOR_VALUE_USER;

public class SurfaceViewMiniMap extends SurfaceView implements SurfaceHolder.Callback{

    private int
            size,
            margin;

    private float
            mapTileSide,
            miniMapTileSizeSmall,
            miniMapTileSizeBig;

    private Rect
            smallRect,
            bigRect;

    private Paint
            painMiniMapSmall = new Paint(),
            colorUser = new Paint(),
            colorAlly = new Paint(),
            colorEnemy = new Paint();

    private Player[] players;

    private boolean
            created = false,
            big = false;

    private Bitmap bitmap;

    public SurfaceViewMiniMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setZOrderMediaOverlay(true);
        size = (int) getResources().getDimension(R.dimen.mini_map_size);
        margin = (int) getResources().getDimension(R.dimen.activity_margin_normal);
        smallRect = new Rect(0,0, size, size);
        colorUser.setColor(COLOR_VALUE_USER);
        colorAlly.setColor(COLOR_VALUE_ALLY);
        colorEnemy.setColor(COLOR_VALUE_ENEMY);
        painMiniMapSmall.setAlpha(0xaa);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        created = true;

        bigRect = new Rect(size + margin, 0, size + margin + getHeight(), getHeight());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void setData(Bitmap map, Player[] mPlayers, int realMapTileSize){
        bitmap = map;
        players = mPlayers;
        mapTileSide = realMapTileSize;

        miniMapTileSizeSmall = (1f * size) / bitmap.getWidth();
        miniMapTileSizeBig = (1f * getHeight()) /bitmap.getWidth();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getX() < size && event.getY() < size){
            big = !big;
            return true;
        }
        return false;
    }

    public void render(){
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas();
            synchronized (getHolder()) {
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    if (big){
                        canvas.drawBitmap(bitmap, null, bigRect, null);
                        canvas.drawBitmap(BitmapManager.getBitmap(R.drawable.cross), null, smallRect, painMiniMapSmall);
                        drawPlayers(canvas, bigRect, miniMapTileSizeBig);
                    }else{
                        canvas.drawBitmap(bitmap, null, smallRect, painMiniMapSmall);
                        drawPlayers(canvas, smallRect, miniMapTileSizeSmall);
                    }
                }
            }
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawPlayers(Canvas canvas, Rect rect, float tileSide){
        Paint currentColor;
        for (Player player:players){
            if (player instanceof User){
                currentColor = colorUser;
            } else if (player.isAlly()){
                currentColor = colorAlly;
            }else{
                currentColor = colorEnemy;
            }/*else{
                continue;
            }*/
            canvas.drawCircle(rect.left + player.getPosition().getFloatX() * tileSide, rect.top + player.getPosition().getFloatY() * tileSide, player.getRadius() / mapTileSide * tileSide, currentColor);
        }
    }

    public boolean isCreated() {
        return created;
    }
}
