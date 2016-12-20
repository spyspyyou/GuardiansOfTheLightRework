package mobile.data.usage.spyspyyou.layouttesting.ui.events;

import android.app.Activity;
import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.JoinActivity;
import mobile.data.usage.spyspyyou.layouttesting.utils.GameInformation;

public class GameInformationAnswerEvent extends UIEvent {

    private final GameInformation GAME_INFO;

    public GameInformationAnswerEvent(String receptor, GameInformation gameInformation) {
        super(new String[]{receptor});
        GAME_INFO = gameInformation;
    }

    /*package*/ GameInformationAnswerEvent(String eventString){
        super(eventString);
        Log.i("Event", "Reading GameInformationAnswerEvent");
        eventString = eventString.substring(eventString.indexOf(ADDRESS_STOP_INDICATOR)+2);
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
        return super.toString() + 'A' + GAME_INFO.toString();
    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }
}
