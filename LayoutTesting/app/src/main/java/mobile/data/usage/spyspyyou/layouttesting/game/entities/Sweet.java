package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.Game;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public class Sweet extends Entity {

    private boolean eatingRequested = false;

    public Sweet(Vector2D entityPosition) {
        super(entityPosition, R.drawable.sweet);
    }

    @Override
    public void update(Game game) {
        if (!eatingRequested && position.squareDistance(Game.getUserPosition()) < 0.25f){
            eatingRequested = true;
            visible = false;
            //todo: request Sweet
        }
    }
}
