package mobile.data.usage.spyspyyou.gametest.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.gametest.R;
import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.GameData;
import mobile.data.usage.spyspyyou.gametest.game.GameServer;
import mobile.data.usage.spyspyyou.gametest.game.PlayerData;
import mobile.data.usage.spyspyyou.gametest.game.World;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.ID_GHOST;
import static mobile.data.usage.spyspyyou.gametest.teststuff.VARS.TEST_WORLD;
import static mobile.data.usage.spyspyyou.gametest.teststuff.VARS.USER_AD;

public class GameActivity extends AppCompatActivity {

    private static Game game = null;
    private static Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_game);
        resources = getResources();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //todo:decide server or not
        game = new GameServer(findViewById(R.id.relativeLayout_game));
        ArrayList<String>addresses = new ArrayList<>();
        addresses.add(USER_AD);
        ArrayList<Boolean>teamBlue = new ArrayList<>();
        teamBlue.add(true);
        ArrayList<Byte>charIds = new ArrayList<>();
        charIds.add(ID_GHOST);
        prepareGame(new GameData(new World(TEST_WORLD), new PlayerData(addresses, teamBlue, charIds)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (game != null)game.stopGame();
        game = null;
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

    public static Resources getRec(){
        return resources;
    }

    public static void prepareGame(GameData gameData){
        game.prepare(gameData);
    }

    public static void startGame(){
        game.start();
    }
}

