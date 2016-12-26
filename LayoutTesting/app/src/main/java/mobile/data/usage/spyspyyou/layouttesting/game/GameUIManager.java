package mobile.data.usage.spyspyyou.layouttesting.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.View;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewJoystick;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewMiniMap;

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

    /*package*/ void renderGame(Canvas canvas){
        surfaceViewGame.drawToScreen(canvas);
    }

    /*package*/ void renderJoystick(){
        surfaceViewJoystick.render();
    }

    /*package*/ void renderMiniMap(){
        surfaceViewMiniMap.render();
    }

    public Vector2D getUserVelocity(){
        return surfaceViewJoystick.getUserVelocity();
    }

    public double getUserDirection(){
        if (surfaceViewGame.hasUserFocus())return surfaceViewGame.getUserDirection();
        return surfaceViewJoystick.getUserDirection();
    }

    public int getTileSide(){
        return surfaceViewGame.getTileSide();
    }

    public boolean areSurfacesCreated(){
        return surfaceViewGame.isCreated() && surfaceViewJoystick.isCreated() && surfaceViewMiniMap.isCreated();
    }

}
