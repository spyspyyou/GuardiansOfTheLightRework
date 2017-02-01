package mobile.data.usage.spyspyyou.gametest.game.events.local;

import mobile.data.usage.spyspyyou.layouttesting.game.Game;

public class GumButtonClickedEvent extends LocalEvent {

    @Override
    public void apply(Game game) {
        game.shootParticle();
    }
}
