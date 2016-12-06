package mobile.data.usage.spyspyyou.layouttesting.ui.ui_events;

import android.support.annotation.Nullable;
import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Event;

public abstract class UIEvent extends Event {

    public UIEvent(String[] receptors) {
        super(receptors);
    }

    public UIEvent(String eventString){
        super(eventString);
    }

    @Override
    public String toString() {
        return 'U' + super.toString();
    }

    @Nullable
    public static UIEvent fromEventString(String eventString){
        Log.i("Event", "Reading UIEvent");
        switch(eventString.charAt(eventString.indexOf(ADDRESS_STOP_INDICATOR)+1)){
            case 'A': return new GameInformationAnswerEvent(eventString);
            case 'B': return new KickPlayerEvent(eventString);
            case 'C': return new TeamRequestEvent(eventString);
            case 'D': return new TeamAnswerEvent(eventString);
            case 'G': return new GameCanceledEvent(eventString);
            case 'J': return new JoinRequestEvent(eventString);
            case 'K': return new JoinAnswerEvent(eventString);
            case 'L': return new LobbyLeftEvent(eventString);
            case 'M': return new ChatEvent(eventString);
            case 'R': return new GameInformationRequestEvent(eventString);
            case 'S': return new JoinSuccessfulEvent(eventString);
            case 'T': return new TeamChangedEvent(eventString);
            default: return null;
        }
    }
}
