package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.ui.adapters.GameInformationAdapter;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.ChatMessage;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.JoinRequest;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.PlayerInfo;

public class ClientLobbyActivity extends LobbyActivity {

    private final AppBluetoothManager.ConnectionListener connectionListener = new AppBluetoothManager.ConnectionListener() {
        @Override
        public void onConnectionEstablished(BluetoothDevice bluetoothDevice) {
            new JoinRequest(sharedPreferences.getString(StartActivity.PREF_NAME, "Client Name"), sharedPreferences.getInt(StartActivity.PREF_PIC, 0)).send(bluetoothDevice.getAddress());
        }

        @Override
        public void onConnectionFailed(BluetoothDevice bluetoothDevice) {
            finish();
        }

        @Override
        public void onConnectionClosed(BluetoothDevice bluetoothDevice) {
            finish();
        }
    };
    private static LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HOST = false;
        Bundle extras = getIntent().getExtras();
        HOST_ADDRESS = extras.getString(GameInformationAdapter.HOST_EXTRA);
        AppBluetoothManager.joinGame(HOST_ADDRESS, connectionListener);
        loadingDialog = new LoadingDialog();
        loadingDialog.show(getSupportFragmentManager(), "Joining Game");
        loadingDialog.setCancelable(false);
    }

    @Override
    protected void send() {
        new ChatMessage("Client Name", editTextMessage.getText().toString()).send(HOST_ADDRESS);
        editTextMessage.setText("");
    }

    public static class LoadingDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.dialog_joining, null));

            return builder.create();
        }
    }

    public static void joinAnswer(boolean answer){
        if (answer)loadingDialog.dismiss();
        else AppBluetoothManager.disconnectFrom(HOST_ADDRESS);
    }

    public static void updateTeams(ArrayList<PlayerInfo> blue, ArrayList<PlayerInfo> green){
        teamBlue.setData(blue);
        teamGreen.setData(green);
    }

}
