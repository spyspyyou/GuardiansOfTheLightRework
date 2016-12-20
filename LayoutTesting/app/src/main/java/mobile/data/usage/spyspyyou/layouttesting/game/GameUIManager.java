package mobile.data.usage.spyspyyou.layouttesting.game;

import android.graphics.Canvas;
import android.view.View;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewJoystick;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewMiniMap;

public class GameUIManager{

    private SurfaceViewGame surfaceViewGame;
    private SurfaceViewJoystick surfaceViewJoystick;
    private SurfaceViewMiniMap surfaceViewMiniMap;

    public GameUIManager(View rootView){
        surfaceViewGame = (SurfaceViewGame) rootView.findViewById(R.id.surfaceView_game);
        surfaceViewJoystick = (SurfaceViewJoystick) rootView.findViewById(R.id.surfaceView_joystick);
        surfaceViewMiniMap = (SurfaceViewMiniMap) rootView.findViewById(R.id.surfaceView_miniMap);
    }

    public Canvas getGameCanvas(){
        return surfaceViewGame.getMatchingCanvas();
    }

    public void renderGame(Canvas canvas){
        surfaceViewGame.render(canvas);
    }

    public void renderJoystick(){
        surfaceViewJoystick.render();
    }

    public void renderMiniMap(){
        surfaceViewMiniMap.render();
    }
}
