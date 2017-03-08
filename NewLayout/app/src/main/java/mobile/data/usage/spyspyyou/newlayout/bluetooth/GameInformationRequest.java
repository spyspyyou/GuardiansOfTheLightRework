package mobile.data.usage.spyspyyou.newlayout.bluetooth;

import mobile.data.usage.spyspyyou.newlayout.ui.activity.ServerLobbyActivity;

public class GameInformationRequest extends Message {

    private final String REQUEST_ADDRESS;

    public GameInformationRequest(String requestAddress){
        REQUEST_ADDRESS = requestAddress;
    }

    @Override
    protected void onReception() {
        GameInformation gameInformation = ServerLobbyActivity.getGameInformation();
        if (gameInformation != null)gameInformation.send(REQUEST_ADDRESS);
        else ConnectionManager.disconnect(REQUEST_ADDRESS);
    }
}
