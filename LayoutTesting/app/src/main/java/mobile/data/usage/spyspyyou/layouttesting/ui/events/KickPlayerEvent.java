package mobile.data.usage.spyspyyou.layouttesting.ui.events;

import android.app.Activity;
import android.content.Intent;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.OnPostEventSending;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyClientActivity;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyHostActivity;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.MainActivity;
import mobile.data.usage.spyspyyou.layouttesting.utils.PlayerInformation;

public class KickPlayerEvent extends UIEvent implements OnPostEventSending{

    private final PlayerInformation PLAYER_INFORMATION;

    public KickPlayerEvent(PlayerInformation playerInformation) {
        super(new String[]{playerInformation.getADDRESS()});
        PLAYER_INFORMATION = playerInformation;
    }

    /*package*/ KickPlayerEvent(String eventString) {
        super(eventString);
        PLAYER_INFORMATION = null;
    }

    @Override
    public String toString() {
        return super.toString() + 'B';
    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }

    @Override
    public void handle() {
        Activity activity = App.accessActiveActivity(null);
        if (activity instanceof LobbyClientActivity){
            activity.startActivity(new Intent(activity.getBaseContext(), MainActivity.class));
            AppBluetoothManager.disconnect(SENDER_ADDRESS);
            //todo:snackbar information
        }
    }

    @Override
    public void onPostSending() {
        AppBluetoothManager.disconnect(PLAYER_INFORMATION.getADDRESS());
        Activity activity = App.accessActiveActivity(null);
        if (activity instanceof LobbyHostActivity){
            ((LobbyHostActivity) activity).onPlayerLeft(PLAYER_INFORMATION);
        }
    }
}


