package mobile.data.usage.spyspyyou.layouttesting.ui.activities;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.utils.ChatHandler;
import mobile.data.usage.spyspyyou.layouttesting.utils.GameInformation;
import mobile.data.usage.spyspyyou.layouttesting.utils.PlayerAdapter;
import mobile.data.usage.spyspyyou.layouttesting.utils.PlayerInformation;

import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.TEAM_BLUE;

public abstract class LobbyActivity extends GotLActivity {

    protected byte team;
    protected static GameInformation gameInformation;
    protected ArrayList<PlayerInformation> teamBlue = new ArrayList<>();
    protected ArrayList<PlayerInformation> teamGreen = new ArrayList<>();
    protected PlayerAdapter blueListAdapter, greenListAdapter;
    protected ListView blueTeamList, greenTeamList;
    protected DrawerLayout drawerLayout;
    protected RelativeLayout relativeLayoutGameInfo;
    protected ChatHandler chatHandler = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_lobby);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_lobby);
        relativeLayoutGameInfo = (RelativeLayout) findViewById(R.id.relativeLayout_gameInfo);
        blueTeamList = (ListView) findViewById(R.id.listView_lobby_teamBlue);
        greenTeamList = (ListView) findViewById(R.id.listView_lobby_teamGreen);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) Log.i("LTest", "Action Bar is null");

        chatHandler = new ChatHandler(this, drawerLayout);

        if (team == TEAM_BLUE) teamBlue.add(new PlayerInformation());
        else teamGreen.add(new PlayerInformation());
        blueListAdapter = new PlayerAdapter(this, teamBlue);
        greenListAdapter = new PlayerAdapter(this, teamGreen);
        blueTeamList.setAdapter(blueListAdapter);
        greenTeamList.setAdapter(greenListAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Activity activity = App.accessActiveActivity(null);
        if (activity instanceof PreparationActivity){
            ((PreparationActivity) activity).setCharacters(gameInformation.getCharacters());
        }
    }

    public void updateListViews() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                blueTeamList.setAdapter(blueListAdapter);
                greenTeamList.setAdapter(greenListAdapter);
                blueListAdapter.notifyDataSetChanged();
                greenListAdapter.notifyDataSetChanged();
            }
        });
    }

    public static void setGameInformation(GameInformation mGameInformation) {
        gameInformation = mGameInformation;
    }

    public void addMessage(final String senderName, final String message, final String senderAddress) {
        if (chatHandler == null){
            Log.w("LobbyActivity", "No chat handler existing");
            return;
        }
        chatHandler.addMessage(senderName, message, senderAddress);
    }
}
