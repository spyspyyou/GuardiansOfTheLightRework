package mobile.data.usage.spyspyyou.layouttesting.ui.ui_events;

import android.app.Activity;
import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.DataCenter;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyActivity;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyHostActivity;

public class ChatEvent extends UIEvent {

    private final String MESSAGE, SENDER_NAME, ORIGINAL_SENDER;

    public ChatEvent(String[] receptors, String message) {
        super(receptors);
        MESSAGE = message;
        SENDER_NAME = DataCenter.getUserName();
        ORIGINAL_SENDER = AppBluetoothManager.getAddress();
    }

    private ChatEvent(LobbyHostActivity lobbyHostActivity, String message, String senderName, String originalSender){
        super(lobbyHostActivity.getAddresses());
        MESSAGE = message;
        SENDER_NAME = senderName;
        ORIGINAL_SENDER = originalSender;
    }

    /*package*/ ChatEvent(String eventString) {
        super(eventString);
        eventString = eventString.substring(eventString.indexOf(ADDRESS_STOP_INDICATOR)+2);
        ORIGINAL_SENDER = eventString.substring(0, eventString.indexOf('-'));
        SENDER_NAME = eventString.substring(eventString.indexOf('-') + 1, eventString.indexOf('\n'));
        MESSAGE = eventString.substring(eventString.indexOf('\n')+1);
    }

    @Override
    public String toString() {
        return super.toString() + 'M' + ORIGINAL_SENDER + '-' + SENDER_NAME + '\n' + MESSAGE;
    }

    @Override
    public void handle() {
        Activity activity = App.accessActiveActivity(null);
        if (activity instanceof LobbyActivity){
            ((LobbyActivity) activity).addMessage(SENDER_NAME, MESSAGE, ORIGINAL_SENDER);
            Log.i("ChatEvent", "added message:" + MESSAGE);
            if (activity instanceof LobbyHostActivity) new ChatEvent(((LobbyHostActivity) activity), MESSAGE, SENDER_NAME, ORIGINAL_SENDER).send();
        }
    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }

}
