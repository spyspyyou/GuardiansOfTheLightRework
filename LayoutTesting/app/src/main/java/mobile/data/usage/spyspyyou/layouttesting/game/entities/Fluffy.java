package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import mobile.data.usage.spyspyyou.layouttesting.game.GameUIManager;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public class Fluffy extends User {

    public Fluffy(Vector2D entityPosition, boolean isVisible, byte characterType, GameUIManager mGameUIManager) {
        super(entityPosition, isVisible, characterType, mGameUIManager);
    }

    @Override
    public void activateSkill() {

    }
}
