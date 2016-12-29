package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import mobile.data.usage.spyspyyou.layouttesting.game.GameUIManager;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_FLUFFY;

public class Fluffy extends User {

    public Fluffy(Vector2D entityPosition, int size, GameUIManager mGameUIManager) {
        super(entityPosition, size, ID_FLUFFY, mGameUIManager);
    }

    @Override
    public void activateSkill() {

    }
}
