package mobile.data.usage.spyspyyou.gametest.game.events.global;


import mobile.data.usage.spyspyyou.gametest.game.Game;

public class PauseEvent extends GlobalEvent {
    @Override
    public void apply(Game game) {
        if (game.isPaused())game.resume();
        else game.pause();
    }
}
