package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

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
    protected void onStop() {
        super.onStop();
        gameInformation = null;
    }

    public static GameInformation getGameInformation(){
        return gameInformation;
    }
}
