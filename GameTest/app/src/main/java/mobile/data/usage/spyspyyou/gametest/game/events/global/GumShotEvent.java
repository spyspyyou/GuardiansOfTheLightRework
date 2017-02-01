package mobile.data.usage.spyspyyou.gametest.game.events.global;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

public class GumShotEvent extends GlobalEvent {

    private final Vector2D
            POSITION,
            VELOCITY;

    public GumShotEvent(Vector2D userPosition, Vector2D userVelocity){
        POSITION = userPosition.copy();
        VELOCITY = userVelocity.copy();
    }

    @Override
    public void apply(Game game) {
        game.addGum(POSITION, VELOCITY);
    }
}
