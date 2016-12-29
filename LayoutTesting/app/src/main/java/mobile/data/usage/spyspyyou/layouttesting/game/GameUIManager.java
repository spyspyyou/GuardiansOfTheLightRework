package mobile.data.usage.spyspyyou.layouttesting.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.View;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.Player;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewJoystick;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewMiniMap;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public class GameUIManager{

    private SurfaceViewGame surfaceViewGame;
    private SurfaceViewJoystick surfaceViewJoystick;
    private SurfaceViewMiniMap surfaceViewMiniMap;

    /*package*/ GameUIManager(View rootView){
        surfaceViewGame = (SurfaceViewGame) rootView.findViewById(R.id.surfaceView_game);
        surfaceViewJoystick = (SurfaceViewJoystick) rootView.findViewById(R.id.surfaceView_joystick);
        surfaceViewMiniMap = (SurfaceViewMiniMap) rootView.findViewById(R.id.surfaceView_miniMap);
    }

    /*package*/ Canvas getGameCanvas(){
        return surfaceViewGame.getCanvas();
    }

    /*package*/ SurfaceHolder getGameSurfaceHolder(){
        return  surfaceViewGame.getHolder();
    }

    /*package*/ void update() {
        surfaceViewJoystick.updateData();
    }

    /*package*/ void render(Canvas canvas){
        renderGame(canvas);
        renderJoystick();
        renderMiniMap();
    }

    private void renderGame(Canvas canvas){
        surfaceViewGame.drawToScreen(canvas);
    }

    private void renderJoystick(){
        surfaceViewJoystick.render();
    }

    private void renderMiniMap(){
        surfaceViewMiniMap.render();
    }

    public VelocityVector2D getUserVelocity(){
        return surfaceViewJoystick.getUserVelocity();
    }

    public double getUserDirection(){
        if (surfaceViewGame.hasUserFocus())return surfaceViewGame.getUserDirection();
        return surfaceViewJoystick.getUserDirection();
    }

    public boolean activeUserDirection(){
        return surfaceViewGame.hasUserFocus() || surfaceViewJoystick.isActive();
    }

    public int getTileSide(){
        return surfaceViewGame.getTileSide();
    }

    public boolean areSurfacesCreated(){
        return surfaceViewGame.isCreated() && surfaceViewJoystick.isCreated() && surfaceViewMiniMap.isCreated();
    }

    public void setData(Bitmap map, Player[]players){
        surfaceViewMiniMap.setData(map, players, getTileSide());
    }

    public Vector2D getGameSurfaceCenter(){
        return new Vector2D(surfaceViewGame.getHalfWidth(), surfaceViewGame.getHalfHeight());
    }

}
