package mobile.data.usage.spyspyyou.layouttesting.ui.ui_events;

import android.app.Activity;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Connection;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.JoinActivity;
import mobile.data.usage.spyspyyou.layouttesting.utils.GameInformation;

public class GameInformationAnswerEvent extends UIEvent {

    private final GameInformation GAME_INFO;

    public GameInformationAnswerEvent(String receptor, GameInformation gameInformation) {
        super(new String[]{receptor});
        GAME_INFO = gameInformation;
    }

    public GameInformationAnswerEvent(String eventString){
        super(eventString);
        GAME_INFO = GameInformation.fromString(eventString, SENDER_ADDRESS);
    }

    @Override
    public void handle() {
        Activity joinActivity = App.accessActiveActivity(null);
        if (joinActivity instanceof JoinActivity){
            ((JoinActivity)(joinActivity)).setDrawerGameInfo(GAME_INFO);
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
