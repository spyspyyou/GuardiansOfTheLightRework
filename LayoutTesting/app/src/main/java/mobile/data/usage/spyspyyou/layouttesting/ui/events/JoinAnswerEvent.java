package mobile.data.usage.spyspyyou.layouttesting.ui.events;

import android.app.Activity;
import android.content.Intent;

import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.JoinActivity;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyActivity;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyClientActivity;

import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ADDRESS_EXTRA;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.NO_TEAM;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.TEAM_EXTRA;

public class JoinAnswerEvent extends UIEvent {

    private final byte TEAM;

    public JoinAnswerEvent(String receptor, byte team) {
        super(new String []{receptor});
        TEAM = team;
    }

    /*package*/ JoinAnswerEvent(String eventString) {
        super(eventString);
        TEAM = Byte.parseByte(eventString.substring(eventString.length() - 1));
    }

    @Override
    public String toString() {
        return super.toString() + 'K' + TEAM;
    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }

    @Override
    public void handle() {
        Activity activity = App.accessActiveActivity(null);
        if (TEAM == NO_TEAM){
            //todo:show snackbar notification
            return;
        }
        if (activity instanceof JoinActivity){
            Intent startLobbyAsClient = new Intent(activity.getBaseContext(), LobbyClientActivity.class);
            LobbyActivity.setGameInformation(JoinActivity.getCurrentGameInformation());
            startLobbyAsClient.putExtra(TEAM_EXTRA, TEAM);
            startLobbyAsClient.putExtra(ADDRESS_EXTRA, SENDER_ADDRESS);
            activity.startActivity(startLobbyAsClient);
        }
    }

}
