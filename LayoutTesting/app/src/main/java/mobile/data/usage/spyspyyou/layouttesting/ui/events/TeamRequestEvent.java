package mobile.data.usage.spyspyyou.layouttesting.ui.events;

import android.app.Activity;

import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyHostActivity;

public class TeamRequestEvent extends UIEvent {

    private final byte TEAM;

    public TeamRequestEvent(String receptor, byte team) {
        super(new String[]{receptor});
        TEAM = team;
    }

    /*package*/ TeamRequestEvent(String eventString) {
        super(eventString);
        TEAM = Byte.parseByte(eventString.substring(eventString.length() - 1));
    }

    @Override
    public String toString() {
        return super.toString() + 'C' + TEAM;
    }

    @Override
    public void handle() {
        Activity activity = App.accessActiveActivity(null);
        if (activity instanceof LobbyHostActivity){
            ((LobbyHostActivity) activity).requestTeam(SENDER_ADDRESS, TEAM);
        }
    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }
}
