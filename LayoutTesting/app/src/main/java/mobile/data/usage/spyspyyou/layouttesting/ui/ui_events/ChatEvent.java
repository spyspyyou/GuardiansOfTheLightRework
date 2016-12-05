package mobile.data.usage.spyspyyou.layouttesting.ui.ui_events;

public class ChatEvent extends UIEvent {

    public ChatEvent(String[] receptors) {
        super(receptors);
    }

    /*package*/ ChatEvent(String eventString) {
        super(eventString);
    }

    @Override
    public void handle() {

    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }

}
