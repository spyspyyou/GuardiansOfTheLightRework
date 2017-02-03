package mobile.data.usage.spyspyyou.gametest.game.events.global;


import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.events.GameEvent;

public abstract class GlobalEvent extends GameEvent {

    public GlobalEvent(){

    }

    protected GlobalEvent(String eventString) {
        super(eventString);
    }

    @Override
    public void onReception() {
        super.onReception();
        if (Game.HOST)send();
    }
}

