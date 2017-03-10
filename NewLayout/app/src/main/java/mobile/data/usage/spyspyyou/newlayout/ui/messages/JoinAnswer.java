package mobile.data.usage.spyspyyou.newlayout.ui.messages;

import mobile.data.usage.spyspyyou.newlayout.bluetooth.Message;
import mobile.data.usage.spyspyyou.newlayout.ui.activity.ClientLobbyActivity;

public class JoinAnswer extends Message {

    private final boolean ACCEPTED;

    public JoinAnswer(boolean accepted){
        ACCEPTED = accepted;
    }

    @Override
    protected void onReception() {
        ClientLobbyActivity.joinAnswer(ACCEPTED);
    }
}
