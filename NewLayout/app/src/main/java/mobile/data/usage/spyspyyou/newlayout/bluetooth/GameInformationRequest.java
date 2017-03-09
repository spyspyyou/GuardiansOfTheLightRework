package mobile.data.usage.spyspyyou.newlayout.bluetooth;

import android.util.Log;

import mobile.data.usage.spyspyyou.newlayout.ui.activity.ServerLobbyActivity;

public class GameInformationRequest extends Message {

    private final String REQUEST_ADDRESS;

    public GameInformationRequest(String requestAddress){
        REQUEST_ADDRESS = requestAddress;
        if (requestAddress == null) Log.e("GIRequest", "request address is null");
    }

    @Override
    protected void onReception() {
        Log.i("GIRequest", "received from " + REQUEST_ADDRESS);
        GameInformation gameInformation = ServerLobbyActivity.getGameInformation();
        if (gameInformation != null)gameInformation.send(REQUEST_ADDRESS);
        else ConnectionManager.disconnect(REQUEST_ADDRESS);
    }
}
