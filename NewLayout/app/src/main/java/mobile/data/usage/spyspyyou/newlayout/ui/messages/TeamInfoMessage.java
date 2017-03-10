package mobile.data.usage.spyspyyou.newlayout.ui.messages;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.newlayout.bluetooth.Message;
import mobile.data.usage.spyspyyou.newlayout.ui.activity.ClientLobbyActivity;

public class TeamInfoMessage extends Message {

    private final ArrayList<PlayerInfo>
            TEAM_BLUE,
            TEAM_GREEN;

    public TeamInfoMessage(ArrayList<PlayerInfo> teamBlue, ArrayList<PlayerInfo> teamGreen){
        TEAM_BLUE = teamBlue;
        TEAM_GREEN = teamGreen;
    }

    @Override
    protected void onReception() {
        ClientLobbyActivity.updateTeams(TEAM_BLUE, TEAM_GREEN);
    }
}
