package mobile.data.usage.spyspyyou.gametest.game.events.global;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.events.server.GumClientEvent;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

public class GumShotEvent extends GlobalEvent {

    private static final String
            KEY_X = "x",
            KEY_Y = "y",
            KEY_DIRECTION = "d",
            KEY_SHOOT_TICK = "st",
            KEY_TEAM_BLUE = "tb",
            KEY_ID = "id";

    private final int
            ID,
            SHOOT_TICK;

    private final Vector2D
            POSITION;

    private final double DIRECTION;

    private final boolean TEAM_BLUE;

    public GumShotEvent(String dataString) throws InvalidMessageException {
        super(dataString);
        POSITION = new Vector2D(getDouble(KEY_X), getDouble(KEY_Y));
        DIRECTION = getDouble(KEY_DIRECTION);
        SHOOT_TICK = getInt(KEY_SHOOT_TICK);
        TEAM_BLUE = getBoolean(KEY_TEAM_BLUE);
        ID = getInt(KEY_ID);
    }

    public GumShotEvent(GumClientEvent gumClientEvent, int id){
        POSITION = gumClientEvent.POSITION;
        DIRECTION = gumClientEvent.DIRECTION;
        SHOOT_TICK = gumClientEvent.SHOOT_TICK;
        TEAM_BLUE = gumClientEvent.TEAM_BLUE;
        ID = id;
    }

    @Override
    public void send() {
        putObject(KEY_X, POSITION.x);
        putObject(KEY_Y, POSITION.y);
        putObject(KEY_DIRECTION, DIRECTION);
        putObject(KEY_SHOOT_TICK, SHOOT_TICK);
        putObject(KEY_TEAM_BLUE, TEAM_BLUE);
        putObject(KEY_ID, ID);
        super.send();
    }

    @Override
    public void apply(Game game) {
        game.addGum(POSITION, DIRECTION, SHOOT_TICK, ID, TEAM_BLUE);
    }
}
