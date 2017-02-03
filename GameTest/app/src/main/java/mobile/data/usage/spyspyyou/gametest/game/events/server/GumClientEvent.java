package mobile.data.usage.spyspyyou.gametest.game.events.server;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.events.global.GumShotEvent;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

public class GumClientEvent extends ServerEvent {

    private static final String
            KEY_X = "x",
            KEY_Y = "y",
            KEY_DIRECTION = "d",
            KEY_SHOOT_TICK = "st",
            KEY_TEAM_BLUE = "tb";

    public final Vector2D POSITION;
    public final double DIRECTION;
    public final int SHOOT_TICK;
    public final boolean TEAM_BLUE;

    public GumClientEvent(String dataString){
        super(dataString);
        POSITION = new Vector2D(getDouble(KEY_X), getDouble(KEY_Y));
        DIRECTION = getDouble(KEY_DIRECTION);
        SHOOT_TICK = getInt(KEY_SHOOT_TICK);
        TEAM_BLUE = getBoolean(KEY_TEAM_BLUE);
    }

    public GumClientEvent(Vector2D position, double direction, int shootTick, boolean teamBlue){
        POSITION = position.copy();
        DIRECTION = direction;
        SHOOT_TICK = shootTick;
        TEAM_BLUE = teamBlue;
    }

    @Override
    public void send() {
        putObject(KEY_X, POSITION.x);
        putObject(KEY_Y, POSITION.y);
        putObject(KEY_DIRECTION, DIRECTION);
        putObject(KEY_SHOOT_TICK, SHOOT_TICK);
        putObject(KEY_TEAM_BLUE, TEAM_BLUE);
        super.send();
    }

    @Override
    public void apply(Game game) {
        new GumShotEvent(this, game.nextGumId()).send();
    }
}
