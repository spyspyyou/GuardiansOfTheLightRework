package mobile.data.usage.spyspyyou.layouttesting.ui.events;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyClientActivity;
import mobile.data.usage.spyspyyou.layouttesting.utils.PlayerInformation;

public class TeamChangedEvent extends UIEvent {

    private final ArrayList<PlayerInformation> BLUE_TEAM, GREEN_TEAM;
    private final byte RECEPTOR_TEAM;

    public TeamChangedEvent(String receptors, ArrayList<PlayerInformation> blueTeam, ArrayList<PlayerInformation> greenTeam, byte receptorTeam) {
        super(new String[]{receptors});
        BLUE_TEAM = blueTeam;
        GREEN_TEAM = greenTeam;
        RECEPTOR_TEAM = receptorTeam;
    }

    /*package*/ TeamChangedEvent(String eventString) {
        super(eventString);
        BLUE_TEAM = new ArrayList<>();
        GREEN_TEAM = new ArrayList<>();
        eventString = eventString.substring(eventString.indexOf(ADDRESS_STOP_INDICATOR) + 2);
        RECEPTOR_TEAM = Byte.parseByte(eventString.substring(0, eventString.indexOf('i')));
        String blues = eventString.substring(eventString.indexOf('i') + 1, eventString.indexOf('-'));
        while(blues.length() > 0){
            BLUE_TEAM.add(PlayerInformation.fromString(blues.substring(0, blues.indexOf('.'))));
            blues = blues.substring(blues.indexOf('.') + 1);
        }
        String greens = eventString.substring(eventString.indexOf('-') + 1);
        while(greens.length() > 0){
            GREEN_TEAM.add(PlayerInformation.fromString(greens.substring(0, greens.indexOf('.'))));
            greens = greens.substring(greens.indexOf('.') + 1);
        }

        Log.i("TCEvent", "blue count: " + BLUE_TEAM.size() + ", green count:"  + GREEN_TEAM.size());
    }

    @Override
    public String toString() {
        String string = super.toString() + 'T' + RECEPTOR_TEAM + 'i';
        for (PlayerInformation playerInformation:BLUE_TEAM){
            string += playerInformation.toString() + '.';
        }
        string += '-';
        for (PlayerInformation playerInformation:GREEN_TEAM){
            string += playerInformation.toString() + '.';
        }
        return  string;
    }

    @Override
    public void handle() {
        Log.i("TCEvent", "handling");
        Activity activity = App.accessActiveActivity(null);
        if (activity instanceof LobbyClientActivity){
            ((LobbyClientActivity)(activity)).updateListViews(BLUE_TEAM, GREEN_TEAM);
            ((LobbyClientActivity) activity).setTeam(RECEPTOR_TEAM);
        }else {
            Log.i("TCEvent", "not in LobbyClientActivity");
        }
    }

    @Override
    public void onEventSendFailure(String[] connections) {

    }
}
