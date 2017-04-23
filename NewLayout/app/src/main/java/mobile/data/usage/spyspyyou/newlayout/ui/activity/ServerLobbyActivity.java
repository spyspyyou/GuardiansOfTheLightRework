package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.GameInformation;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.ChatMessage;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.JoinAnswer;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.JoinRequest;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.TeamInfoMessage;

import static mobile.data.usage.spyspyyou.newlayout.game.GameVars.MAX_TEAM_SIZE;

public class ServerLobbyActivity extends LobbyActivity {

    //todo:null when starting selection
    private static GameInformation gameInformation = null;
    private static ArrayList<String> regConListeners = new ArrayList<>();
    private AppBluetoothManager.BluetoothActionListener connectionMadeListener = new AppBluetoothManager.BluetoothActionListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onGameSearchStarted() {

        }

        @Override
        public void onGameSearchFinished() {

        }

        @Override
        public void onConnectionEstablished(String address) {
            regConListeners.add(address);
            AppBluetoothManager.addConnectionListener(address, connectionListener);
        }
    };
    private boolean startedGame = false;
    private boolean selectionStarted = false;

    private AppBluetoothManager.ConnectionListener connectionListener = new AppBluetoothManager.ConnectionListener() {
        @Override
        public void onConnectionEstablished(BluetoothDevice bluetoothDevice) {

        }

        @Override
        public void onConnectionFailed(BluetoothDevice bluetoothDevice) {

        }

        @Override
        public void onConnectionClosed(BluetoothDevice bluetoothDevice) {
            onDisconnect(bluetoothDevice.getAddress());
            regConListeners.remove(bluetoothDevice.getAddress());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HOST = true;
        gameInformation = StartActivity.getGameInformation();
        teamBlue.addPlayer(sharedPreferences.getString(StartActivity.PREF_NAME, "Host Name"), AppBluetoothManager.getLocalAddress(), sharedPreferences.getInt(StartActivity.PREF_PIC, 0));
        HOST_ADDRESS = AppBluetoothManager.getLocalAddress();
        ListView
                blue = (ListView) findViewById(R.id.listView_activityLobby_blue),
                green = (ListView) findViewById(R.id.listView_activityLobby_green);

        blue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!selectionStarted && !teamBlue.getItem(position).ADDRESS.equals(AppBluetoothManager.getLocalAddress())) kickPlayerDialog(teamBlue.getItem(position).NAME, teamBlue.getItem(position).ADDRESS);
            }
        });

        green.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!selectionStarted && !teamGreen.getItem(position).ADDRESS.equals(AppBluetoothManager.getLocalAddress())) kickPlayerDialog(teamGreen.getItem(position).NAME, teamGreen.getItem(position).ADDRESS);
            }
        });

        ImageButton imageButtonStartSelection = (ImageButton) findViewById(R.id.imageButton_activityLobby_startGame);
        imageButtonStartSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelection();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppBluetoothManager.REQUEST_BLUETOOTH) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Bluetooth is required.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                AppBluetoothManager.startServer(this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void startSelection(){
        selectionStarted = true;
    }

    private void resetLobby(){

    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBluetoothManager.startServer(this);
        AppBluetoothManager.addBluetoothListener(connectionMadeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppBluetoothManager.removeBluetoothListener(connectionMadeListener);
        gameInformation = null;
        AppBluetoothManager.stopServer();
        for (String conListener : regConListeners) AppBluetoothManager.removeConnectionListener(conListener, connectionListener);
        if (!startedGame)AppBluetoothManager.releaseRequirements(this);
    }

    @Override
    protected void send() {
        ChatMessage message = new ChatMessage("Host Name", editTextMessage.getText().toString());
        if (selectionStarted){
            if (teamBlue.hasPlayer(AppBluetoothManager.getLocalAddress()))message.send(teamBlue.getPlayerAddresses());
            else message.send(teamGreen.getPlayerAddresses());
        }else {
            message.send(teamBlue.getPlayerAddresses());
            message.send(teamGreen.getPlayerAddresses());
        }
        editTextMessage.setText("");
    }

    private void kickPlayerDialog(String playerName, final String address){
        new AlertDialog.Builder(getBaseContext())
                .setTitle("Kick Player")
                .setMessage("Would you like to kick " + playerName)
                .setPositiveButton("Be gone", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppBluetoothManager.disconnectFrom(address);
                    }
                })
                .setNegativeButton("Nah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public static GameInformation getGameInformation() {
        return gameInformation;
    }

    public static void requestJoin(JoinRequest joinRequest) {
        if (gameInformation != null) {
            if (teamBlue.getCount() <= MAX_TEAM_SIZE) {
                new JoinAnswer(true).send(joinRequest.REQUEST_ADDRESS);
                teamBlue.addPlayer(joinRequest.PLAYER_NAME, joinRequest.REQUEST_ADDRESS, joinRequest.PIC);
                updateTeams();
            } else if (teamGreen.getCount() <= MAX_TEAM_SIZE){
                new JoinAnswer(true).send(joinRequest.REQUEST_ADDRESS);
                teamGreen.addPlayer(joinRequest.PLAYER_NAME, joinRequest.REQUEST_ADDRESS, joinRequest.PIC);
                updateTeams();
            } else {
                new JoinAnswer(false).send(joinRequest.REQUEST_ADDRESS);
            }
        } else {
            AppBluetoothManager.disconnectFrom(joinRequest.REQUEST_ADDRESS);
        }
    }

    public static void updateTeams() {
        TeamInfoMessage teamInfoMessage = new TeamInfoMessage(teamBlue.getData(), teamGreen.getData());
        teamInfoMessage.send(teamBlue.getPlayerAddresses());
        teamInfoMessage.send(teamGreen.getPlayerAddresses());
    }

    public void onDisconnect(String address) {
        if (selectionStarted){
            Snackbar.make(findViewById(R.id.imageButton_activityLobby_green), "A Player has left the game.", Snackbar.LENGTH_LONG);
            resetLobby();
        }
        if (teamBlue.removePlayer(address) != null || teamGreen.removePlayer(address) != null)
            updateTeams();
    }

    public static void requestTeam(String address, boolean blue) {
        if (blue && teamBlue.getCount() < MAX_TEAM_SIZE) {
            teamBlue.addPlayer(teamGreen.removePlayer(address));
        } else if (!blue && teamGreen.getCount() < MAX_TEAM_SIZE) {
            teamGreen.addPlayer(teamBlue.removePlayer(address));
        }
        updateTeams();
    }
}
