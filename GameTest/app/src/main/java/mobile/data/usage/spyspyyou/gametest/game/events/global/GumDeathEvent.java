package mobile.data.usage.spyspyyou.gametest.game.events.global;

import mobile.data.usage.spyspyyou.gametest.game.Game;

public class GumDeathEvent extends GlobalEvent {

    private static final String KEY_ID = "id";

    private final int ID;

    public GumDeathEvent(String dataString){
        super(dataString);
        ID = getInt(KEY_ID);
    }

    public GumDeathEvent(int id){
        ID = id;
    }

    @Override
    public void send() {
        putObject(KEY_ID, ID);
        super.send();
    }

    @Override
    public void apply(Game game) {
        game.removeGum();
    }
}
