package mobile.data.usage.spyspyyou.gametest.game;

import android.support.annotation.NonNull;

import static mobile.data.usage.spyspyyou.gametest.teststuff.bluetooth.Messenger.SUB_SEPARATION_CHAR;

public class GameData {

    public final World WORLD = null;

    @NonNull
    public final PlayerData PLAYER_DATA = null;


    public GameData(String dataString){
        /*
        WORLD = new World(dataString.substring(0, dataString.indexOf(SUB_SEPARATION_CHAR)));
        PLAYER_DATA = new PlayerData(dataString.substring(dataString.indexOf(SUB_SEPARATION_CHAR) + 1));
        */
    }

    private GameData(World world, PlayerData playerData){
        /*
        WORLD = world;
        PLAYER_DATA = playerData;
        */
    }

    @Override
    public String toString(){
        return WORLD.toString() + SUB_SEPARATION_CHAR + PLAYER_DATA.toString();
    }
}
