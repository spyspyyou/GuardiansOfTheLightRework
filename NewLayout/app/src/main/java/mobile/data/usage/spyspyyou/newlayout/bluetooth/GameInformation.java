package mobile.data.usage.spyspyyou.newlayout.bluetooth;

import android.util.Log;

import mobile.data.usage.spyspyyou.newlayout.game.World;

public class GameInformation extends Message {

    public final String
            HOST_ADDRESS,
            GAME_NAME;
    //public final World WORLD;
    public final boolean
            CHARACTERS_UNIQUE,
            ALLOWED_FLUFFY,
            ALLOWED_SLIME,
            ALLOWED_GHOST,
            ALLOWED_NOX;
    public final int
            PLAYER_MAX,
            SWEET_REGEN,
            MANA_REGEN,
            SELECTION_TIME;

    public GameInformation(String address, String gameName, World world, boolean charactersUnique, boolean allowedFluffy, boolean allowedSlime, boolean allowedGhost, boolean allowedNox, int playerMax, int sweetRegen, int manaRegen, int selectionTime){
        HOST_ADDRESS = address;
        GAME_NAME = gameName;
        //WORLD = world;
        CHARACTERS_UNIQUE = charactersUnique;
        ALLOWED_FLUFFY = allowedFluffy;
        ALLOWED_SLIME = allowedSlime;
        ALLOWED_GHOST = allowedGhost;
        ALLOWED_NOX = allowedNox;
        PLAYER_MAX = playerMax;
        SWEET_REGEN = sweetRegen;
        MANA_REGEN = manaRegen;
        SELECTION_TIME = selectionTime;
    }
    @Override
    protected void onReception() {
        Log.i("GameInformation", "received");
        AppBluetoothManager.addGame(this);
        ConnectionManager.disconnect(HOST_ADDRESS);
    }
}
