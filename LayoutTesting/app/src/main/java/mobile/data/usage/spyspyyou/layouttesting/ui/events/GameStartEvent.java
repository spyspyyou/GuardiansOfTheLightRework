package mobile.data.usage.spyspyyou.layouttesting.ui.events;

public class GameStartEvent extends UIEvent {

    public GameStartEvent(String[] receptors) {
        super(receptors);
    }

    /*package*/ GameStartEvent(String eventString) {
        super(eventString);
    }

    @Override
    public void handle() {

    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }
}
