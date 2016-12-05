package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.DataCenter;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.GameCanceledEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.JoinAnswerEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.KickPlayerEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.TeamAnswerEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.TeamChangedEvent;
import mobile.data.usage.spyspyyou.layouttesting.utils.GameInformation;
import mobile.data.usage.spyspyyou.layouttesting.utils.PlayerInformation;
import mobile.data.usage.spyspyyou.layouttesting.utils.ViewDataSetters;

import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.NO_TEAM;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.TEAM_BLUE;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.TEAM_GREEN;

public class LobbyHostActivity extends LobbyActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //clear up the connections so 7 people can join the game
        AppBluetoothManager.disconnect();
        gameInformation = new GameInformation(DataCenter.getMapWidth(), DataCenter.getMapHeight(), DataCenter.getAllowedPlayerTypes());
        ViewDataSetters.setGameInfo(gameInformation, relativeLayoutGameInfo);

        team = TEAM_BLUE;
        blueTeamList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (teamBlue.get(position).getADDRESS() == null)return;
                new AlertDialog.Builder(App.accessActiveActivity(null))
                        .setTitle("Kick Player")
                        .setMessage("Would you like to just kick( or also block {not yet available})the player?")
                        .setPositiveButton("Kick", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new KickPlayerEvent(teamBlue.remove(position)).send();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }
                ).show();
            }
        });
        greenTeamList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (teamGreen.get(position).getADDRESS() == null)return;
                new AlertDialog.Builder(App.accessActiveActivity(null))
                        .setTitle("Kick Player")
                        .setMessage("Would you like to just kick( or also block {not yet available})the player?")
                        .setPositiveButton("Kick", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new KickPlayerEvent(teamGreen.remove(position)).send();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }
                ).show();
            }
        });
    }

    @Override
    protected void onResume() {
        activeActivityRequiresServer = true;
        super.onResume();
        DataCenter.setAppStatus(DataCenter.HOSTING);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (App.accessActiveActivity(null) instanceof MainActivity) {
            DataCenter.setAppStatus(DataCenter.DEFAULT);
            AppBluetoothManager.disconnect();
        }
    }

    @Override
    public void onConnectionLost() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_lobby_host, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public GameInformation getGameInformation(){
        return gameInformation;
    }

    public void requestEntrance(String address){
        //todo:add check if they are friends
        byte team = getTeamWithSpace();
        if (team == TEAM_BLUE)teamBlue.add(new PlayerInformation(address));
        else teamGreen.add(new PlayerInformation(address));
        new JoinAnswerEvent(address, team).send();
        updateListViews();
    }

    private byte getTeamWithSpace(){
        if (teamBlue.size() < 4)return TEAM_BLUE;
        if (teamGreen.size() < 4)return TEAM_GREEN;
        return NO_TEAM;
    }

    public void requestTeam(String address, byte team){
        PlayerInformation playerInformation = findPlayerInformation(address);
        switch (team){
            case TEAM_BLUE:
                if (!teamBlue.contains(playerInformation) && teamBlue.size() < 4){
                    new TeamAnswerEvent(address, TEAM_BLUE).send();
                    teamGreen.remove(playerInformation);
                    teamBlue.add(playerInformation);
                }else{
                    new TeamAnswerEvent(address, NO_TEAM).send();
                }
                break;
            case TEAM_GREEN:
                if (!teamGreen.contains(playerInformation) && teamGreen.size() < 4){
                    new TeamAnswerEvent(address, TEAM_BLUE).send();
                    teamBlue.remove(playerInformation);
                    teamGreen.add(playerInformation);
                }else{
                    new TeamAnswerEvent(address, NO_TEAM).send();
                }
                break;
        }
        updateListViews();
    }

    @Nullable
    private PlayerInformation findPlayerInformation(String address){
        for (PlayerInformation playerInformation:teamBlue){
            if (playerInformation.getADDRESS() == null)continue;
            if (playerInformation.getADDRESS().equals(address))return playerInformation;
        }
        for (PlayerInformation playerInformation:teamGreen){
            if (playerInformation.getADDRESS() == null)continue;
            if (playerInformation.getADDRESS().equals(address))return playerInformation;
        }
        return null;
    }

    private String[] getAddresses(){
        String[] connections = new String[teamBlue.size() + teamGreen.size()];

        // no exception to worry much but if the sizes of the array change during the reading process it causes an exception
        try {
            int i;
            for (i = 0; i < teamBlue.size(); ++i) {
                connections[i] = teamBlue.get(i).getADDRESS();
            }
            for (int j = i; j - i < teamGreen.size(); ++j) {
                connections[j] = teamGreen.get(j - i).getADDRESS();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return connections;
    }

    private void sendTeamChangedEvent(){
        for (PlayerInformation playerInformation:teamBlue){
            if (playerInformation.getADDRESS() == null)continue;
            new TeamChangedEvent(playerInformation.getADDRESS(), teamBlue, teamGreen, TEAM_BLUE).send();
        }
        for (PlayerInformation playerInformation:teamGreen){
            if (playerInformation.getADDRESS() == null)continue;
            new TeamChangedEvent(playerInformation.getADDRESS(), teamBlue, teamGreen, TEAM_GREEN).send();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_close:
                new GameCanceledEvent(getAddresses());
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                break;
            case R.id.action_swapTeam:
                if (team == TEAM_BLUE && teamGreen.size() < 4){
                    teamBlue.remove(findPlayerInformation(null));
                    teamGreen.add(findPlayerInformation(null));
                    //todo:snackBar information
                }else if (team == TEAM_GREEN && teamBlue.size() < 4){
                    teamGreen.remove(findPlayerInformation(null));
                    teamBlue.add(findPlayerInformation(null));
                    //todo:snackBar information
                }
                updateListViews();
                break;
            case R.id.action_startGame:
                //todo:start the game
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateListViews() {
        super.updateListViews();
        sendTeamChangedEvent();
    }

    private void removeClientFromList(PlayerInformation playerInformation){
        teamBlue.remove(playerInformation);
        teamGreen.remove(playerInformation);
    }

    public void onPlayerLeft(String address){
        for (PlayerInformation playerInformation:teamBlue){
            if (playerInformation.getADDRESS() == null)continue;
            if (playerInformation.getADDRESS().equals(address))onPlayerLeft(playerInformation);
        }
        for (PlayerInformation playerInformation:teamGreen){
            if (playerInformation.getADDRESS() == null)continue;
            if (playerInformation.getADDRESS().equals(address))onPlayerLeft(playerInformation);
        }
    }

    public void onPlayerLeft(PlayerInformation playerInformation){
        removeClientFromList(playerInformation);
        updateListViews();
        sendTeamChangedEvent();
    }
}
