package mobile.data.usage.spyspyyou.layouttesting.game.events.global;

import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public class AddSlimeEvent extends GlobalEvent {

    private final Vector2D POSITION;
    private final int BIRTH_TICK;

    public AddSlimeEvent(Vector2D position, int birthTick) {
        POSITION = position;
        BIRTH_TICK = birthTick;
    }

    @Override
    public void apply(Game game) {
        game.addSlime(POSITION, BIRTH_TICK);
    }
}
