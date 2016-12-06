package mobile.data.usage.spyspyyou.layouttesting.ui.ui_events;

import android.content.Intent;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.OnPostEventSending;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.MainActivity;

public class GameCanceledEvent extends UIEvent implements OnPostEventSending{

    public GameCanceledEvent(String[] receptors) {
        super(receptors);
    }

    /*package*/ GameCanceledEvent(String eventString) {
        super(eventString);
    }

    @Override
    public String toString() {
        return super.toString() + 'G';
    }

    @Override
    public void handle() {
        App.accessActiveActivity(null).startActivity(new Intent(App.getContext(), MainActivity.class));
        //todo.snackbar information
    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }

    @Override
    public void onPostSending() {
        AppBluetoothManager.disconnect();
        App.accessActiveActivity(null).startActivity(new Intent(App.getContext(), MainActivity.class));
    }
}
