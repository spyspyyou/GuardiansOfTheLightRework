package mobile.data.usage.spyspyyou.newlayout.bluetooth;

import mobile.data.usage.spyspyyou.newlayout.ui.activity.ServerLobbyActivity;

public class GameInformationRequest extends Messenger {

    private static final String KEY_REQUEST_ADDRESS = "ra";

    private final String REQUEST_ADDRESS;

    public GameInformationRequest(String message){
        super(message);
        REQUEST_ADDRESS = getString(KEY_REQUEST_ADDRESS);
    }

    public GameInformationRequest(String receptor, String requestAddress){
        super(new String[]{receptor});
        REQUEST_ADDRESS = requestAddress;
        putObect(KEY_REQUEST_ADDRESS, REQUEST_ADDRESS);
    }

    @Override
    protected void onReception() {
        GameInformation gameInformation = ServerLobbyActivity.getGameInformation();
        if (gameInformation != null)gameInformation.send(REQUEST_ADDRESS);
        else ConnectionManager.disconnect(REQUEST_ADDRESS);
    }
}
