package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.GameInformation;

public class ServerLobbyActivity extends LobbyActivity {

    //todo:null when starting selection
    private static GameInformation gameInformation = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameInformation = StartActivity.getGameInformation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppBluetoothManager.REQUEST_BLUETOOTH){
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), "Bluetooth is required.", Toast.LENGTH_LONG).show();
                finish();
            }else{
                AppBluetoothManager.startServer(this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBluetoothManager.startServer(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameInformation = null;
        AppBluetoothManager.stopServer(this);
    }

    public static GameInformation getGameInformation(){
        return gameInformation;
    }
}
