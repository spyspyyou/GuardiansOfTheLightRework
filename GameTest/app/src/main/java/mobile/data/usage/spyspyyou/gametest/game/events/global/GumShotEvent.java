package mobile.data.usage.spyspyyou.gametest.game.events.global;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

public class GumShotEvent extends GlobalEvent {

    private static final String
            KEY_X = "x",
            KEY_Y = "y",
            KEY_VX = "vx",
            KEY_VY = "vy",
            KEY_ID = "id";

    private final int ID;

    private final Vector2D
            POSITION,
            VELOCITY;

    public GumShotEvent(String dataString) throws InvalidMessageException {
        super(dataString);
        POSITION = new Vector2D(getDouble(KEY_X), getDouble(KEY_Y));
        VELOCITY = new Vector2D(getDouble(KEY_VX), getDouble(KEY_VY));
        ID = getInt(KEY_ID);
    }

    public GumShotEvent(Vector2D userPosition, Vector2D userVelocity, int id){
        POSITION = userPosition.copy();
        VELOCITY = userVelocity.copy();
        ID = id;
    }

    @Override
    public void send() {
        putObject(KEY_X, POSITION.x);
        putObject(KEY_Y, POSITION.y);
        putObject(KEY_VX, VELOCITY.x);
        putObject(KEY_VY, VELOCITY.y);
        putObject(KEY_ID, ID);
        super.send();
    }

    @Override
    public void apply(Game game) {
        game.addGum(POSITION, VELOCITY, ID);
    }
}
