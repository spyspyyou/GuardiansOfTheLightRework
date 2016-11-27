package mobile.data.usage.spyspyyou.layouttesting.ui.ui_events;

import android.app.Activity;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Connection;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyActivity;


public class GameInformationRequestEvent extends UIEvent {

    public GameInformationRequestEvent(Connection[] receptors){
        super(receptors);
    }

    public GameInformationRequestEvent(String eventString){
        super(eventString);
    }

    @Override
    public void handle() {
        Activity activity = App.accessActiveActivity(null);
        if (activity instanceof LobbyActivity) {
            LobbyActivity lobbyActivity = (LobbyActivity) activity;
            new GameInformationAnswerEvent(SENDER_ADDRESS, lobbyActivity.getGameInformation()).send();
        }
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public void onEventSendFailure(Connection[] connections) {

    }
}
