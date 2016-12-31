package mobile.data.usage.spyspyyou.layouttesting.ui.activities;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.BitmapManager;
import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.game.events.GameEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewGame;

public class GameActivity extends GotLActivity {

    private Game game = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_game);
        game = new Game(getResources(), findViewById(R.id.relativeLayout_game), BitmapFactory.decodeResource(getResources(), R.drawable.test_map_3), SurfaceViewGame.LEFT);
    }

    @Override
    protected void onResume() {
        activeActivityRequiresServer = false;
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BitmapManager.clearMemory();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUI();
    }

    private void hideSystemUI(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void addEvent(GameEvent gameEvent){
        game.addEvent(gameEvent);
    }
}
