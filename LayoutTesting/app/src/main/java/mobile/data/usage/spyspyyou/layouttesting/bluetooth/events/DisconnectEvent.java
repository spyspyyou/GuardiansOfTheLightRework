package mobile.data.usage.spyspyyou.layouttesting.bluetooth.events;

import android.content.Intent;

import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.GotLActivity;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.JoinActivity;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyClientActivity;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyHostActivity;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.MainActivity;

public class DisconnectEvent extends BluetoothEvent {

    public DisconnectEvent(String[] addresses) {
        super(addresses);
    }

    public DisconnectEvent(String eventString) {
        super(eventString);
    }

    @Override
    public String toString() {
        return super.toString() + 'D';
    }

    @Override
    public void handle() {
        GotLActivity activity = App.accessActiveActivity(null);
        //todo:add snackbar info
        if (activity instanceof LobbyHostActivity){
            ((LobbyHostActivity) activity).onPlayerLeft(SENDER_ADDRESS);
        }else if (activity instanceof LobbyClientActivity){
            if (SENDER_ADDRESS.equals(((LobbyClientActivity) activity).getHostAddress())){
                activity.startActivity(new Intent(activity.getBaseContext(), MainActivity.class));
            }
        }else if (activity instanceof JoinActivity){
            ((JoinActivity) activity).search();
        }
    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }
}
