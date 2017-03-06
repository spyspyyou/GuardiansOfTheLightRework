package mobile.data.usage.spyspyyou.gametest.game.events.global;

import mobile.data.usage.spyspyyou.gametest.game.Game;

public class ResumeEvent extends GlobalEvent {
    @Override
    public void apply(Game game) {
        game.resume();
    }
}
