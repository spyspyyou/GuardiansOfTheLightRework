package mobile.data.usage.spyspyyou.gametest.game.events.global;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

public class AddSlimeEvent extends GlobalEvent {

    private static final String
            KEY_X = "x",
            KEY_Y = "y",
            KEY_BT = "bt",
            KEY_ID = "id";

    private final int
            BIRTH_TICK,
            ID;

    private final Vector2D POSITION;

    public AddSlimeEvent(String dataString) throws InvalidMessageException {
        super(dataString);
        POSITION = new Vector2D(getDouble(KEY_X), getDouble(KEY_Y));
        BIRTH_TICK = getInt(KEY_BT);
        ID = getInt(KEY_ID);
    }

    public AddSlimeEvent(Vector2D position, int birthTick, int id) {
        POSITION = position;
        BIRTH_TICK = birthTick;
        ID = id;
    }

    @Override
    public void send() {
        putObject(KEY_X, POSITION.x);
        putObject(KEY_Y, POSITION.y);
        putObject(KEY_BT, BIRTH_TICK);
        putObject(KEY_ID, ID);
        super.send();
    }

    @Override
    public void apply(Game game) {
        game.addSlime(POSITION, BIRTH_TICK, ID);
    }
}
