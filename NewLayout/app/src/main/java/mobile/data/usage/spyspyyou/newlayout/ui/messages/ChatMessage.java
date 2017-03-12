package mobile.data.usage.spyspyyou.newlayout.ui.messages;

import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.Message;
import mobile.data.usage.spyspyyou.newlayout.ui.activity.LobbyActivity;

public class ChatMessage extends Message {

    public final String
            ADDRESS,
            NAME,
            MESSAGE;

    public ChatMessage(String name, String message){
        ADDRESS = AppBluetoothManager.getLocalAddress();
        NAME = name;
        MESSAGE = message;
        if (LobbyActivity.HOST)onReception();
    }

    @Override
    protected void onReception() {
        LobbyActivity.messageReceived(this);
    }
}
