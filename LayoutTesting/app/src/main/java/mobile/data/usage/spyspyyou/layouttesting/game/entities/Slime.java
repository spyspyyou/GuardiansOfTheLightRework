package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import mobile.data.usage.spyspyyou.layouttesting.game.GameUIManager;
import mobile.data.usage.spyspyyou.layouttesting.game.Vector2D;

public class Slime extends User {

    public Slime(Vector2D entityPosition, boolean isVisible, byte characterType, GameUIManager mGameUIManager) {
        super(entityPosition, isVisible, characterType, mGameUIManager);
    }

    @Override
    public void activateSkill() {

    }
}
