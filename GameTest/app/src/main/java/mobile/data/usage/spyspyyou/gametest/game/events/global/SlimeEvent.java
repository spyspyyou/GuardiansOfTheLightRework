package mobile.data.usage.spyspyyou.gametest.game.events.global;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.TICK;

public class SlimeEvent extends GlobalEvent {

    private static final int LIFE_SPAN_TICKS = 5 * TICK;

    private static final String
            KEY_X = "x",
            KEY_Y = "y",
            KEY_DT = "dt";

    private final int DEATH_TICK;

    private final Vector2D POSITION;

    public SlimeEvent(String dataString) throws InvalidMessageException {
        super(dataString);
        POSITION = new Vector2D(getDouble(KEY_X), getDouble(KEY_Y));
        DEATH_TICK = getInt(KEY_DT);
    }

    public SlimeEvent(Vector2D position, int birthTick) {
        POSITION = position;
        DEATH_TICK = birthTick + LIFE_SPAN_TICKS;
    }

    @Override
    public void send() {
        putObject(KEY_X, POSITION.x);
        putObject(KEY_Y, POSITION.y);
        putObject(KEY_DT, DEATH_TICK);
        super.send();
    }

    @Override
    public void apply(Game game) {
        game.addSlimeTrail(POSITION, DEATH_TICK);
    }
}
