package mobile.data.usage.spyspyyou.newlayout.ui.messages;

import android.support.annotation.NonNull;

import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.Message;
import mobile.data.usage.spyspyyou.newlayout.ui.activity.ServerLobbyActivity;

public class TeamRequest extends Message {

    private final boolean TEAM_BLUE;
    private final String ADDRESS;

    public TeamRequest(boolean teamBlue){
        TEAM_BLUE = teamBlue;
        ADDRESS = AppBluetoothManager.getLocalAddress();
    }

    @Override
    public void send(@NonNull String[] receptors) {
        if (receptors[0].equals(AppBluetoothManager.getLocalAddress()))onReception();
        else super.send(receptors);
    }

    @Override
    protected void onReception() {
        ServerLobbyActivity.requestTeam(ADDRESS, TEAM_BLUE);
    }
}
