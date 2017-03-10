package mobile.data.usage.spyspyyou.newlayout.ui.messages;

import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.Message;
import mobile.data.usage.spyspyyou.newlayout.ui.activity.ServerLobbyActivity;

public class JoinRequest extends Message {

    public final String
            REQUEST_ADDRESS,
            PLAYER_NAME;
    public final int PIC;

    public JoinRequest(String playerName, int pic){
        REQUEST_ADDRESS = AppBluetoothManager.getLocalAddress();
        PLAYER_NAME = playerName;
        PIC = pic;
    }

    @Override
    protected void onReception() {
        ServerLobbyActivity.requestJoin(this);
    }

}