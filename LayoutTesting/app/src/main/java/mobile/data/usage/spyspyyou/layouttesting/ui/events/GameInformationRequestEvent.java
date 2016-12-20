package mobile.data.usage.spyspyyou.layouttesting.ui.events;

import android.app.Activity;
import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyHostActivity;


public class GameInformationRequestEvent extends UIEvent {

    public GameInformationRequestEvent(String[] receptors){
        super(receptors);
    }

    /*package*/ GameInformationRequestEvent(String eventString){
        super(eventString);
        Log.i("Event", "Reading GameInformationRequestEvent");
    }

    @Override
    public void handle() {
        Log.i("GIREvent", "handling");
        Activity activity = App.accessActiveActivity(null);
        if (activity instanceof LobbyHostActivity) {
            LobbyHostActivity lobbyActivity = (LobbyHostActivity) activity;
            new GameInformationAnswerEvent(SENDER_ADDRESS, lobbyActivity.getGameInformation()).send();
        }
    }

    @Override
    public String toString() {
        return super.toString()+ 'R';
    }

    @Override
    public void onEventSendFailure(String[] connections) {
        Log.i("GIREvent", "event failed to be sent");
    }
}
