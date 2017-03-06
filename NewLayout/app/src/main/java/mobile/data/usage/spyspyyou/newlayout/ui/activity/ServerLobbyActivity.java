package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import mobile.data.usage.spyspyyou.newlayout.bluetooth.GameInformation;

public class ServerLobbyActivity extends LobbyActivity {

    //todo:null when starting selection
    private static GameInformation gameInformation = null;

    @Override
    protected void onStart() {
        super.onStart();
        //todo:gameInfo set
        gameInformation = null;
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
