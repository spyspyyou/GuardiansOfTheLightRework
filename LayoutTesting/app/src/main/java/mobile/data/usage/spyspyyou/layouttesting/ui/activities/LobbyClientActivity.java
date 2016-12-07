package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.JoinSuccessfulEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.LobbyLeftEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.TeamRequestEvent;
import mobile.data.usage.spyspyyou.layouttesting.utils.PlayerAdapter;
import mobile.data.usage.spyspyyou.layouttesting.utils.PlayerInformation;
import mobile.data.usage.spyspyyou.layouttesting.utils.ViewDataSetters;

import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ADDRESS_EXTRA;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.NO_TEAM;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.TEAM_BLUE;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.TEAM_EXTRA;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.TEAM_GREEN;

public class LobbyClientActivity extends LobbyActivity {

    private static final String NO_HOST_ADDRESS = "noHost";

    private String hostAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras == null) Log.e("LobbyActivity", "no extras found");
        else {
            team = extras.getByte(TEAM_EXTRA, NO_TEAM);
            hostAddress = extras.getString(ADDRESS_EXTRA, NO_HOST_ADDRESS);
            if (hostAddress.equals(NO_HOST_ADDRESS))Log.e("LCActivity", "no host connection");
        }
        ViewDataSetters.setGameInfo(gameInformation, relativeLayoutGameInfo);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) drawerLayout.closeDrawers();
        else {
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            new LobbyLeftEvent(new String[]{hostAddress}).send();
            super.onBackPressed();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        new JoinSuccessfulEvent(new String[]{hostAddress}).send();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_lobby_join, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_close:
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                new LobbyLeftEvent(new String[]{hostAddress}).send();
                return true;
            case R.id.action_swapTeam:
                new TeamRequestEvent(hostAddress, (team == TEAM_BLUE)?TEAM_GREEN:TEAM_BLUE).send();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        activeActivityRequiresServer = false;
        super.onResume();
    }

    @Override
    public void onConnectionLost() {

    }

    public void updateListViews(ArrayList<PlayerInformation> blue, ArrayList<PlayerInformation> green) {
        Log.i("LCActivity", "updating the list views, blue: " + blue.size() + ", green: " + green.size());
        teamBlue = blue;
        teamGreen = green;
        blueListAdapter = new PlayerAdapter(this, teamBlue);
        greenListAdapter = new PlayerAdapter(this, teamGreen);
        updateListViews();
    }

    public void setTeam(byte mTeam){
        team = mTeam;
    }

    public String getHostAddress(){
        return hostAddress;
    }
}
