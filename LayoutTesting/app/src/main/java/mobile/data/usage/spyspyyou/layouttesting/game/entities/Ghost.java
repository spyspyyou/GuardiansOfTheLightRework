package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import mobile.data.usage.spyspyyou.layouttesting.game.GameUIManager;

public class Ghost extends User {
    public Ghost(int entityX, int entityY, boolean isVisible, byte type, GameUIManager mGameUIManager) {
        super(entityX, entityY, isVisible, type, mGameUIManager);
    }
}
