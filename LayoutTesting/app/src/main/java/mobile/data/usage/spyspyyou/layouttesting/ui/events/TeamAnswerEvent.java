package mobile.data.usage.spyspyyou.layouttesting.ui.events;

public class TeamAnswerEvent extends UIEvent {

    private final byte TEAM;

    public TeamAnswerEvent(String receptor, byte team) {
        super(new String[]{receptor});
        TEAM = team;
    }

    /*package*/ TeamAnswerEvent(String eventString) {
        super(eventString);
        TEAM = Byte.parseByte(eventString.substring(eventString.length() - 1));
    }

    @Override
    public String toString() {
        return super.toString() + 'D' + TEAM;
    }

    @Override
    public void handle() {
        //todo:snackBar info
        //if the team is NO_TEAM, the request was not accepted
    }

    @Override
    public void onEventSendFailure(String[] addresses) {

    }
}
