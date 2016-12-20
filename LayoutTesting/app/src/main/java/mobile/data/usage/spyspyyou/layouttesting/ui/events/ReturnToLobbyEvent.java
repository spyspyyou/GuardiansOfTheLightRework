package mobile.data.usage.spyspyyou.layouttesting.ui.events;


public class ReturnToLobbyEvent extends UIEvent {

    public ReturnToLobbyEvent(String[] receptors) {
        super(receptors);
    }

    public ReturnToLobbyEvent(String eventString) {
        super(eventString);
    }

    @Override
    public void handle() {

    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }
}
