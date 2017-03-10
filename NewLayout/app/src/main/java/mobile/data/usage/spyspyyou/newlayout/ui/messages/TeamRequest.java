package mobile.data.usage.spyspyyou.newlayout.ui.messages;

import mobile.data.usage.spyspyyou.newlayout.bluetooth.Message;

public class TeamRequest extends Message {

    private final boolean TEAM_BLUE;

    public TeamRequest(boolean teamBlue){
        TEAM_BLUE = teamBlue;
    }

    @Override
    protected void onReception() {

    }
}
