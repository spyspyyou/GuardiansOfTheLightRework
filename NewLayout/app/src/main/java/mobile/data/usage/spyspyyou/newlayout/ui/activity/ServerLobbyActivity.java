package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.GameInformation;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.ChatMessage;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.JoinAnswer;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.JoinRequest;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.TeamInfoMessage;

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
        teamBlue.addPlayer("name", AppBluetoothManager.getLocalAddress(), R.drawable.floor_tile);
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
    }

    @Override
    protected void send() {
        ChatMessage message = new ChatMessage("Host Name", editTextMessage.getText().toString());
        message.send(teamBlue.getPlayerAddresses());
        message.send(teamGreen.getPlayerAddresses());
        editTextMessage.setText("");
    }

    public static GameInformation getGameInformation() {
        return gameInformation;
    }

    public static void requestJoin(JoinRequest joinRequest) {
        if (gameInformation != null) {
            if (teamBlue.getCount() + teamGreen.getCount() < gameInformation.PLAYER_MAX) {
                new JoinAnswer(true).send(joinRequest.REQUEST_ADDRESS);
                if (teamBlue.getCount() < gameInformation.PLAYER_MAX) {
                    teamBlue.addPlayer(joinRequest.PLAYER_NAME, joinRequest.REQUEST_ADDRESS, joinRequest.PIC);
                } else {
                    teamGreen.addPlayer(joinRequest.PLAYER_NAME, joinRequest.REQUEST_ADDRESS, joinRequest.PIC);
                }
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
        if (teamBlue.removePlayer(address) != null || teamGreen.removePlayer(address) != null)
            updateTeams();
    }

    public static void requestTeam(String address, boolean blue) {
        if (blue && teamBlue.getCount() < gameInformation.PLAYER_MAX) {
            teamBlue.addPlayer(teamGreen.removePlayer(address));
        } else if (!blue && teamGreen.getCount() < gameInformation.PLAYER_MAX) {
            teamGreen.addPlayer(teamBlue.removePlayer(address));
        } else {
            //todo:inform of failure
        }
        updateTeams();
    }
}
