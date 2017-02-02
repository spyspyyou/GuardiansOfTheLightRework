package mobile.data.usage.spyspyyou.gametest.game;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import mobile.data.usage.spyspyyou.gametest.game.entities.Fluffy;
import mobile.data.usage.spyspyyou.gametest.game.entities.Ghost;
import mobile.data.usage.spyspyyou.gametest.game.entities.Nox;
import mobile.data.usage.spyspyyou.gametest.game.entities.Player;
import mobile.data.usage.spyspyyou.gametest.game.entities.Slime;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.ID_FLUFFY;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.ID_GHOST;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.ID_NOX;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.ID_SLIME;
import static mobile.data.usage.spyspyyou.gametest.teststuff.VARS.USER_AD;

public class PlayerData {

    private final ArrayList<String> addresses;
    private final ArrayList<Boolean> teamBlue;
    private final ArrayList<Byte> characterIds;

    public PlayerData(String dataString){
        addresses = new ArrayList<>();
        teamBlue = new ArrayList<>();
        characterIds = new ArrayList<>();
    }

    public PlayerData(ArrayList<String> mAddresses, ArrayList<Boolean> mTeamBlue, ArrayList<Byte> mCharacterIds){
        addresses = mAddresses;
        teamBlue = mTeamBlue;
        characterIds = mCharacterIds;
    }

    public Map<String, Player> generatePlayers(){
        boolean userTeamBlue = false;
        for (int i = 0; i < addresses.size(); ++i){
            if (addresses.get(i).equals(USER_AD))userTeamBlue = teamBlue.get(i);
        }

        Map<String, Player> players = new LinkedHashMap<>();
        for (int i = 0; i < addresses.size(); ++i){
            if (addresses.get(i).equals(USER_AD)){
                switch(characterIds.get(i)){
                    case ID_FLUFFY:
                        players.put(addresses.get(i), new Fluffy(teamBlue.get(i)));
                        break;
                    case ID_SLIME:
                        players.put(addresses.get(i), new Slime(teamBlue.get(i)));
                        break;
                    case ID_GHOST:
                        players.put(addresses.get(i), new Ghost(teamBlue.get(i)));
                        break;
                    case ID_NOX:
                        players.put(addresses.get(i), new Nox(teamBlue.get(i)));
                        break;
                }
            }else{
                players.put(addresses.get(i), new Player(teamBlue.get(i), teamBlue.get(i) == userTeamBlue, addresses.get(i), characterIds.get(i)));
            }
        }
        return players;
    }

    @Override
    public String toString() {
        return "";
    }
}
