package mobile.data.usage.spyspyyou.newlayout.bluetooth;

import mobile.data.usage.spyspyyou.newlayout.game.World;

public class GameInformation extends Messenger{

    private static final String
            KEY_ADDRESS = "address",
            KEY_NAME = "name",
            KEY_WORLD = "world",
            KEY_CHARACTERS_UNIQUE = "charUnique",
            KEY_ALLOWED_FLUFFY = "af",
            KEY_ALLOWED_SLIME = "as",
            KEY_ALLOWED_GHOST = "ag",
            KEY_ALLOWED_NOX = "an",
            KEY_PLAYER_MAX = "pm",
            KEY_SWEET_REGEN = "sr",
            KEY_MANA_REGEN = "mr",
            KEY_SELECTION_TIME = "st";

    public final String HOST_ADDRESS;
    public final String GAME_NAME;
    public final World WORLD;
    public final boolean CHARACTERS_UNIQUE;
    public final boolean
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
        WORLD = world;
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

    public GameInformation(String message){
        super(message);
        HOST_ADDRESS = getString(KEY_ADDRESS);
        GAME_NAME = getString(KEY_NAME);
        WORLD = (World) getObject(KEY_WORLD);
        CHARACTERS_UNIQUE = getBoolean(KEY_CHARACTERS_UNIQUE);
        ALLOWED_FLUFFY = getBoolean(KEY_ALLOWED_FLUFFY);
        ALLOWED_SLIME = getBoolean(KEY_ALLOWED_SLIME);
        ALLOWED_GHOST = getBoolean(KEY_ALLOWED_GHOST);
        ALLOWED_NOX = getBoolean(KEY_ALLOWED_NOX);
        PLAYER_MAX = getInt(KEY_PLAYER_MAX);
        SWEET_REGEN = getInt(KEY_SWEET_REGEN);
        MANA_REGEN = getInt(KEY_MANA_REGEN);
        SELECTION_TIME = getInt(KEY_SELECTION_TIME);
    }

    public void send(String receptor) {
        receptors = new String[]{receptor};
        putObect(KEY_ADDRESS, HOST_ADDRESS);
        putObect(KEY_NAME, GAME_NAME);
        putObect(KEY_WORLD, WORLD);
        putObect(KEY_CHARACTERS_UNIQUE, CHARACTERS_UNIQUE);
        putObect(KEY_ALLOWED_FLUFFY, ALLOWED_FLUFFY);
        putObect(KEY_ALLOWED_SLIME, ALLOWED_SLIME);
        putObect(KEY_ALLOWED_GHOST, ALLOWED_GHOST);
        putObect(KEY_ALLOWED_NOX, ALLOWED_NOX);
        putObect(KEY_PLAYER_MAX, PLAYER_MAX);
        putObect(KEY_SWEET_REGEN, SWEET_REGEN);
        putObect(KEY_MANA_REGEN, MANA_REGEN);
        putObect(KEY_SELECTION_TIME, SELECTION_TIME);
        super.send();
    }

    @Override
    protected void onReception() {
        AppBluetoothManager.addGame(this);
        ConnectionManager.disconnect(HOST_ADDRESS);
    }
}
