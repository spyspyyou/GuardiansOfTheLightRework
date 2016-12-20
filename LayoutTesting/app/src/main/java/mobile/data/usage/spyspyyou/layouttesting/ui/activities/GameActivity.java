package mobile.data.usage.spyspyyou.layouttesting.ui.activities;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.BitmapManager;
import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.game.events.GameEvent;

public class GameActivity extends GotLActivity {

    private Game game = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_1);
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
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    public void setupGame(){
        game = new Game(getResources(), findViewById(R.id.relativeLayout_game));
    }

    public void startTick(){
        if (game == null){
            Log.w("GameActivity", "starting nonexistent game");
            return;
        }
        game.startGame();
    }

    public void addEvent(GameEvent gameEvent){
        game.addEvent(gameEvent);
    }
}
